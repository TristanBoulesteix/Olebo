package fr.olebo.plugins

import fr.olebo.sharescene.shareSceneRouting
import fr.olebo.update.releaseRouting
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        static("/static") {
            resources()
        }
        get("/") {
            call.respondText("Hello World!")
        }
        releaseRouting()
        shareSceneRouting()
    }
}
