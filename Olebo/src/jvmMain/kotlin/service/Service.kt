package jdr.exia.service

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*

const val SERVER_URL = "https://olebo.fr/"

val client
    get() = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

val socketClient
    get() = client.config {
        install(WebSockets)
    }