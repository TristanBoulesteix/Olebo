package fr.olebo.plugins

import fr.olebo.sharescene.shareSceneRouting
import fr.olebo.update.releaseRouting
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        static("/static") {
            resources()
        }
        releaseRouting()
        shareSceneRouting()
    }
}
