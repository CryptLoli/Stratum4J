package net.nekocurit.stratum4j.handle.state

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.nekocurit.stratum4j.StratumConnection
import net.nekocurit.stratum4j.packet.s2c.app.response.S2CPacketResponseLoginSuccess

class HandleLoginStatus(private val connection: StratumConnection): SimpleChannelInboundHandler<S2CPacketResponseLoginSuccess>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: S2CPacketResponseLoginSuccess) {
        connection.sessionId = packet.sessionId
        ctx.pipeline().firstContext().fireChannelRead(packet.job)
    }

}