package net.nekocurit.stratum4j

import kotlinx.serialization.json.Json

object ProtocolJson {
    val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
}