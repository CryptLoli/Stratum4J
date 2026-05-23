package net.nekocurit.stratum4j

import io.netty.channel.ChannelHandler
import net.nekocurit.stratum4j.packet.EnumConnectionDirection
import net.nekocurit.stratum4j.packet.c2s.C2SPacketLogin

interface IStratumConnectionSettingsHelper {
    var tcpNoDelay: Boolean
    var timeout: UInt
    var keepalive: UInt
    fun addPipeLine(name: String, handler: ChannelHandler)
    fun onLoginRequest(block: suspend () -> C2SPacketLogin)
    fun onConnected(block: suspend (connection: StratumConnection) -> Unit)
    fun onDisconnected(block: suspend (reason: String, e: Throwable?) -> Unit)
    fun protocolDebugger(block: (direction: EnumConnectionDirection, raw: String) -> Unit)
}