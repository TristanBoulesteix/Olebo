package fr.olebo.plugins

import fr.olebo.model.Version
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

fun Application.configureRouting() {
    // Starting point for a Ktor app:
    routing {
        get("/versions/last") {
            call.respond(Version(1, "Test"))
        }
        get("/versions/{versionCode}") {
            call.respondFile(File(""))
        }
        put("/versions") {
            val versionData = call.receive<Version>()

            val fileData = call.receiveMultipart().readAllParts().firstOrNull()

            if (fileData is PartData.FileItem) {
                fileData.streamProvider().use {
                    File("Olebo_${versionData.versionName}_${versionData.versionCode}").writeBytes(it.readBytes())
                }
            }
        }
    }
}
