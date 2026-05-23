package net.nekocurit.stratum4j.packet.s2c.app.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.nekocurit.stratum4j.packet.S2CPacket

@Serializable
@SerialName("job")
class S2CPacketEventNewJob(
    @SerialName("job_id")
    val jobId: String,
    val id: String = "",
    val target: String,
    val blob: String,
    @SerialName("seed_hash")
    val seed: String,
    val algo: String,
    val height: ULong
): S2CPacket