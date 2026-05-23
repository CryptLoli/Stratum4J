package net.nekocurit.stratum4j.packet.s2c.app.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.nekocurit.stratum4j.packet.S2CPacket
import net.nekocurit.stratum4j.packet.s2c.app.event.S2CPacketEventNewJob

@Serializable
class S2CPacketResponseLoginSuccess(
    @SerialName("id")
    val sessionId: String,
    val job: S2CPacketEventNewJob
): S2CPacket