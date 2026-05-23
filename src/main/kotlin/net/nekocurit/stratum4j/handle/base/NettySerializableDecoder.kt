package net.nekocurit.stratum4j.handle.base

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import net.nekocurit.stratum4j.StratumConnection
import net.nekocurit.stratum4j.packet.EnumConnectionDirection
import net.nekocurit.stratum4j.packet.s2c.base.S2CPacketEvents
import net.nekocurit.stratum4j.packet.s2c.base.S2CPacketResponse
import net.nekocurit.stratum4j.ProtocolJson
import java.nio.charset.Charset

class NettySerializableDecoder(val connection: StratumConnection): ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, into: ByteBuf, out: MutableList<Any>) {
        val raw = into.readLine() ?: return
        connection.protocolDebugger?.invoke(EnumConnectionDirection.CLIENT_BOUND, raw)

        ProtocolJson.json.decodeFromString<DecodeTest>(raw)
            .also { base ->
                when(base.method) {
                    "" -> ProtocolJson.json.decodeFromString<S2CPacketResponse>(raw)
                        .also { response ->
                            connection.responseHandles
                                .remove(response.id)
                                ?.also { out.add(it(response)) }
                        }
                    else -> base.params
                        ?.let { params ->
                            ProtocolJson.json.decodeFromJsonElement(S2CPacketEvents.packets[base.method] as DeserializationStrategy<Any>, params)
                        }
                        ?.also {
                            out.add(it)
                        }
                }
            }

    }

    fun ByteBuf.readLine(charset: Charset = Charsets.UTF_8) = forEachByte { it.toInt().toChar() != '\n' }
        .takeIf { it != -1 }
        ?.let { index ->
            readRetainedSlice(index - readerIndex()).also {
                skipBytes(1)
            }
        }
        ?.let { buf ->
            val line = buf.toString(charset)
            buf.release()

            return@let line
        }



    @Serializable
    class DecodeTest(val method: String = "", val params: JsonElement? = null)
}