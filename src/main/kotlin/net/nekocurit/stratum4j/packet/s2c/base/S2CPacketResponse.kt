package net.nekocurit.stratum4j.packet.s2c.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

@Serializable
class S2CPacketResponse(
    val id: UInt,
    @Suppress("SpellCheckingInspection")
    @SerialName("jsonrpc")
    val version: String = "2.0",
    val result: JsonElement = JsonNull,
    val error: JsonElement = JsonNull
)