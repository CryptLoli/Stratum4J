package net.nekocurit.stratum4j.packet.s2c.app.response

import kotlinx.serialization.Serializable
import net.nekocurit.stratum4j.packet.S2CPacket

@Serializable
data class S2CPacketResponseKeepalive(
    val id: UInt
): S2CPacket