package fr.olebo.update

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import jdr.exia.system.OS
import jdr.exia.system.extension
import java.io.File

fun Route.releaseRouting() {
    route("/releases") {
        get {
            call.respond(releases)
        }
        get("/last") {
            call.respond(releases.last())
        }
        get("/last/download") {
            val os = try {
                OS.valueOf(call.request.queryParameters["os"].orEmpty())
            } catch (e: IllegalArgumentException) {
                OS.OTHER
            }

            val path = releases.last().paths.firstOrNull { it.extension in os.executableFileTypes }.orEmpty()

            val fileToSend = File(path)

            if (fileToSend.exists() && fileToSend.isFile) {
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        "Olebo.${fileToSend.extension}"
                    ).toString()
                )
                call.respondFile(fileToSend)
            } else {
                call.respond(HttpStatusCode.NotFound, "No file found for requested OS (${os.name})")
            }
        }
    }
}