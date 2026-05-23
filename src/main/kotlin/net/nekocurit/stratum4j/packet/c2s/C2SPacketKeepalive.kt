package net.nekocurit.stratum4j.packet.c2s

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.encodeToJsonElement
import net.nekocurit.stratum4j.packet.c2s.base.C2SPacketBase
import net.nekocurit.stratum4j.packet.s2c.app.response.S2CPacketResponseKeepalive
import net.nekocurit.stratum4j.ProtocolJson

@Suppress("SpellCheckingInspection")
@Serializable
data class C2SPacketKeepalive(
    @SerialName("id")
    val sessionId: String
): C2SPacketBase.C2SPacketParamsBase(method = "keepalived", response = { response ->
    S2CPacketResponseKeepalive(response.id)
}) {
    override fun toJson() = ProtocolJson.json.encodeToJsonElement(this)
}