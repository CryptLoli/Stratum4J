package net.nekocurit.stratum4j

import io.netty.channel.ChannelHandler
import net.nekocurit.stratum4j.packet.EnumConnectionDirection
import net.nekocurit.stratum4j.packet.c2s.C2SPacketLogin

class StratumConnectionSettingsHelper(val connection: StratumConnection): IStratumConnectionSettingsHelper {

    override var tcpNoDelay = false
    override var timeout = 30u
    override var keepalive = 0U

    val pipelines = mutableListOf<Pair<String, ChannelHandler>>()

    override fun addPipeLine(name: String, handler: ChannelHandler) {
        pipelines.add(name to handler)
    }

    override fun onLoginRequest(block: suspend () -> C2SPacketLogin) {
        connection.onLoginRequest = block
    }

    override fun onConnected(block: suspend (connection: StratumConnection) -> Unit) {
        connection.onConnected = block
    }

    override fun onDisconnected(block: suspend (reason: String, e: Throwable?) -> Unit) {
        connection.onDisconnected = block
    }

    override fun protocolDebugger(block: (direction: EnumConnectionDirection, raw: String) -> Unit) {
        connection.protocolDebugger = block
    }
}