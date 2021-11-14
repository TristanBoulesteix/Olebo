package fr.olebo.plugins

import fr.olebo.sharescene.ShareScene
import io.ktor.application.*
import io.ktor.sessions.*

fun Application.configureSession() {
    install(Sessions) {
        cookie<ShareScene>("ShareScene")
    }
}