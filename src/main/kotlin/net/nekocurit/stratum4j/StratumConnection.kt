package net.nekocurit.stratum4j

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.*
import io.netty.channel.nio.NioIoHandler
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import kotlinx.coroutines.runBlocking
import net.nekocurit.stratum4j.handle.base.NettySerializableDecoder
import net.nekocurit.stratum4j.handle.base.NettySerializableEncoder
import net.nekocurit.stratum4j.handle.base.StratumKeepAlive
import net.nekocurit.stratum4j.handle.state.HandleLoginStatus
import net.nekocurit.stratum4j.packet.EnumConnectionDirection
import net.nekocurit.stratum4j.packet.S2CPacket
import net.nekocurit.stratum4j.packet.c2s.C2SPacketLogin
import net.nekocurit.stratum4j.packet.c2s.base.C2SPacketBase
import net.nekocurit.stratum4j.packet.s2c.base.S2CPacketResponse

class StratumConnection: ChannelInboundHandlerAdapter(), IStratumConnection {

    var sessionId = ""

    var disconnectReason = ""
    var disconnectException: Throwable? = null

    val responseHandles = hashMapOf<UInt, (S2CPacketResponse) -> S2CPacket>()

    lateinit var channel: Channel

    var onLoginRequest: suspend () -> C2SPacketLogin = { error("No login request set") }
    var onConnected: suspend ((connection: StratumConnection) -> Unit) = { }
    var onDisconnected: suspend ((reason: String, e: Throwable?) -> Unit) = { _, _ -> }
    var protocolDebugger: ((direction: EnumConnectionDirection, raw: String) -> Unit)? = null

    override fun channelActive(ctx: ChannelHandlerContext) {
        channel = ctx.channel()

        runBlocking {
            sendPacket(onLoginRequest())
            onConnected.invoke(this@StratumConnection)
        }

        super.channelActive(ctx)
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        runBlocking { onDisconnected.invoke(disconnectReason, disconnectException) }

        super.channelInactive(ctx)
    }

    override val isConnected: Boolean
        get() = runCatching { channel.isOpen }.getOrElse { false }

    override val isLogin: Boolean
        get() = isConnected && this.sessionId.isNotEmpty()

    override fun close() {
        disconnectReason = "Closed."
        sessionId = ""
        channel.close().awaitUninterruptibly()
    }

    override var packetId = 1U
        private set

    override fun sendPacket(packet: C2SPacketBase.C2SPacketParamsBase) {
        channel.writeAndFlush(packet.toPacket(packetId++)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
    }

    @SafeVarargs
    override fun sendPacket(packet: C2SPacketBase.C2SPacketParamsBase, vararg futureListeners: GenericFutureListener<out Future<in Void>>) {
        channel.writeAndFlush(packet.toPacket(packetId++))
            .apply {
                futureListeners.forEach { addListener(it) }
                addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
            }
    }

    companion object {
        val eventLoopGroup = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())

        fun createStratumConnectionAndConnect(host: String, port: Int, block: suspend IStratumConnectionSettingsHelper.(connection: IStratumConnection) -> Unit = { }): IStratumConnection {
            val connection = StratumConnection()

            runCatching {
                val settings = StratumConnectionSettingsHelper(connection)
                    .also { runBlocking { block(it, connection) }}

                Bootstrap()
                    .group(eventLoopGroup)
                    .handler(
                        object : ChannelInitializer<Channel>() {
                            override fun initChannel(ch: Channel) {
                                ch.pipeline().addLast("timeout", ReadTimeoutHandler(settings.timeout.toInt()))
                                ch.pipeline().addLast("decoder", NettySerializableDecoder(connection))
                                ch.pipeline().addLast("encoder", NettySerializableEncoder(connection))
                                ch.pipeline().addLast("keepalive", StratumKeepAlive(connection, settings.keepalive.toLong()))

                                ch.pipeline().addLast("base", connection)
                                ch.pipeline().addLast("status_login", HandleLoginStatus(connection))

                                settings.pipelines.forEach { (name, handler) ->
                                    ch.pipeline().addLast(name, handler)
                                }
                            }
                        }
                    )
                    .channel(NioSocketChannel::class.java)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.TCP_NODELAY, settings.tcpNoDelay)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .connect(host, port)
                    .syncUninterruptibly()
            }
                .onFailure { e ->
                    connection.channelInactive(null)
                    throw e
                }


            return connection
        }

    }

}
