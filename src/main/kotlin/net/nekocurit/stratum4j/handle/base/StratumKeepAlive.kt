package net.nekocurit.stratum4j.handle.base

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.HashedWheelTimer
import io.netty.util.Timeout
import io.netty.util.TimerTask
import net.nekocurit.stratum4j.StratumConnection
import net.nekocurit.stratum4j.packet.c2s.C2SPacketKeepalive
import java.util.concurrent.TimeUnit

class StratumKeepAlive(val connection: StratumConnection, var delay: Long): TimerTask, ChannelInboundHandlerAdapter() {

    val timer = HashedWheelTimer()

    override fun channelActive(ctx: ChannelHandlerContext) {
        timer.newTimeout(this, delay, TimeUnit.SECONDS)
        super.channelActive(ctx)
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        timer.stop()
        super.channelInactive(ctx)
    }
    
    override fun run(data: Timeout) {
        runCatching {
            if (connection.isLogin) connection.sendPacket(C2SPacketKeepalive(connection.sessionId))
        }
        timer.newTimeout(this, delay, TimeUnit.SECONDS)
    }


}