package net.nekocurit.stratum4j.packet.s2c.base

import net.nekocurit.stratum4j.packet.s2c.app.event.S2CPacketEventNewJob

object S2CPacketEvents {
    val packets = hashMapOf(
        "job" to S2CPacketEventNewJob.serializer()
    )
}