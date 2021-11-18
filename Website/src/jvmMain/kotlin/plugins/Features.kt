package fr.olebo.plugins

import fr.olebo.sharescene.ShareScene
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.sessions.*
import io.ktor.websocket.*

fun Application.configureFeatures() {
    // install(HttpsRedirect)
    install(ContentNegotiation) {
        json()
    }
    install(Sessions) {
        cookie<ShareScene>("ShareScene")
    }
    install(WebSockets)
}