package jdr.exia.service

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*

const val SERVER_URL = "https://olebo.fr/"

val client
    get() = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

val socketClient
    get() = client.config {
        install(WebSockets)
    }