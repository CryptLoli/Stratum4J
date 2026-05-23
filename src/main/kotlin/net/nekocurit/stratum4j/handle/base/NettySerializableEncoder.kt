package net.nekocurit.stratum4j.handle.base

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import net.nekocurit.stratum4j.StratumConnection
import net.nekocurit.stratum4j.packet.EnumConnectionDirection
import net.nekocurit.stratum4j.packet.c2s.base.C2SPacketBase
import net.nekocurit.stratum4j.ProtocolJson

class NettySerializableEncoder(val connection: StratumConnection): MessageToByteEncoder<C2SPacketBase<*>>() {

    @OptIn(InternalSerializationApi::class)
    override fun encode(ctx: ChannelHandlerContext, packet: C2SPacketBase<*>, out: ByteBuf) {
        val raw = ProtocolJson.json.encodeToString(C2SPacketBaseRaw(id = packet.id, method = packet.params.method, params = packet.params.toJson()))

        connection.protocolDebugger?.invoke(EnumConnectionDirection.SERVER_BOUND, raw)
        connection.responseHandles[packet.id] = packet.params.response

        out.writeCharSequence(raw, Charsets.UTF_8)
        out.writeByte('\n'.code)
    }

    @Serializable
    class C2SPacketBaseRaw(
        val id: UInt,
        @Suppress("SpellCheckingInspection")
        @SerialName("jsonrpc")
        val version: String = "2.0",
        val method: String,
        val params: JsonElement
    )

}
