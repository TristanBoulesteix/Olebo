package fr.olebo.plugins

import fr.olebo.model.Version
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

fun Application.configureRouting() {
    // Starting point for a Ktor app:
    routing {
        get("/versions/last") {
            call.respond(Version(1, "Test"))
        }
        get("/versions/{id}") {
            call.respondFile(File(""))
        }
    }
}
