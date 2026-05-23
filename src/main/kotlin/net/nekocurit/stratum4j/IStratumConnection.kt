package net.nekocurit.stratum4j

import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.nekocurit.stratum4j.packet.c2s.base.C2SPacketBase

interface IStratumConnection {
    val isConnected: Boolean
    val isLogin: Boolean
    val packetId: UInt
    fun close()
    fun sendPacket(packet: C2SPacketBase.C2SPacketParamsBase)
    @SafeVarargs
    fun sendPacket(packet: C2SPacketBase.C2SPacketParamsBase, vararg futureListeners: GenericFutureListener<out Future<in Void>>)
}