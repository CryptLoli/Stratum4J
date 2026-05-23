package net.nekocurit.stratum4j.packet.c2s

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import net.nekocurit.stratum4j.packet.c2s.base.C2SPacketBase
import net.nekocurit.stratum4j.packet.s2c.app.response.S2CPacketResponseSubmitAccepted
import net.nekocurit.stratum4j.packet.s2c.app.response.S2CPacketResponseSubmitRejected
import net.nekocurit.stratum4j.ProtocolJson

@Serializable
data class C2SPacketSubmit(
    val id: String,
    @SerialName("job_id")
    val jobId: String,
    val nonce: String,
    val result: String,
    @Transient
    var noteObject: Any? = null
): C2SPacketBase.C2SPacketParamsBase(method = "submit", response = { response ->
    if (response.error is JsonNull) {
        S2CPacketResponseSubmitAccepted(response.id, noteObject)
    } else {
        val result = ProtocolJson.json.decodeFromJsonElement<SimpleError>(response.error)
        result.checkUnauthenticated()
        S2CPacketResponseSubmitRejected(response.id, result.code, result.message, noteObject)
    }
}) {

    override fun toJson() = ProtocolJson.json.encodeToJsonElement(this)
    @Serializable
    class SimpleError(val code: Int, val message: String) {
        fun checkUnauthenticated() {
            if (message == "Unauthenticated") throw Exception("Unauthenticated")
        }
    }
}