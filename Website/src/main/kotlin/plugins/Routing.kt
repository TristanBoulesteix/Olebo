package fr.olebo.plugins

import fr.olebo.update.OLEBO_RELEASES_DIRECTORY
import fr.olebo.update.releaseRouting
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/test") {
            val fileToSend = File(OLEBO_RELEASES_DIRECTORY + File.separator + "OleboSetup.exe")

            if (fileToSend.exists() && fileToSend.isFile) {
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        "Olebo11.${fileToSend.extension}"
                    ).toString()
                )
                call.respondFile(fileToSend)
            } else {
                call.respond(HttpStatusCode.NotFound, "No file found for requested OS")

            }
        }
        releaseRouting()
    }
}
