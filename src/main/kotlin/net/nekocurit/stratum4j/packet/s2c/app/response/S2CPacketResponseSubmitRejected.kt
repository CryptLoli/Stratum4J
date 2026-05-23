package net.nekocurit.stratum4j.packet.s2c.app.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.nekocurit.stratum4j.packet.S2CPacket

@Serializable
class S2CPacketResponseSubmitRejected(
    val id: UInt,
    val code: Int,
    val message: String,
    @Transient
    var noteObject: Any? = null
): S2CPacket