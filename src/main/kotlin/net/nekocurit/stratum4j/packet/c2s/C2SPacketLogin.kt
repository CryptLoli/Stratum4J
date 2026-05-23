package net.nekocurit.stratum4j.packet.c2s

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import net.nekocurit.stratum4j.packet.c2s.base.C2SPacketBase
import net.nekocurit.stratum4j.packet.s2c.app.response.S2CPacketResponseLoginSuccess
import net.nekocurit.stratum4j.ProtocolJson

@Serializable
data class C2SPacketLogin(
    val login: String,
    val pass: String,
    val agent: String,
    val algo: List<String>
): C2SPacketBase.C2SPacketParamsBase(method = "login", response = { response ->
    ProtocolJson.json.decodeFromJsonElement<S2CPacketResponseLoginSuccess>(response.result)
}) {
    override fun toJson() = ProtocolJson.json.encodeToJsonElement(this)
}