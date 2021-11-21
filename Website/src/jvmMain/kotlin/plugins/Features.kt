package fr.olebo.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.websocket.*

fun Application.configureFeatures() {
    // install(HttpsRedirect)
    install(ContentNegotiation) {
        json()
    }
    install(WebSockets)
}