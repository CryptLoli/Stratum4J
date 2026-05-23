package net.nekocurit.stratum4j.packet.c2s.base

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import net.nekocurit.stratum4j.packet.C2SPacket
import net.nekocurit.stratum4j.packet.S2CPacket
import net.nekocurit.stratum4j.packet.s2c.base.S2CPacketResponse

data class C2SPacketBase<T : C2SPacketBase.C2SPacketParamsBase>(
    val id: UInt,
    val params: T
): C2SPacket {

    @Serializable
    abstract class C2SPacketParamsBase(
        @Transient
        val method: String = throw NotImplementedError(),
        @Transient
        val response: (S2CPacketResponse) -> S2CPacket = { throw NotImplementedError() }
    ) {
        fun toPacket(id: UInt) = C2SPacketBase(id = id, params = this)
        abstract fun toJson(): JsonElement
    }
}