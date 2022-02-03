package fr.olebo.update

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import jdr.exia.system.OS
import jdr.exia.system.extension
import java.io.File

private const val VERSION_CODE_KEY = "version_code"

fun Routing.releaseRouting() {
    route("releases") {
        get {
            call.respond(releases)
        }
        route("{$VERSION_CODE_KEY}") {
            get {
                val versionCode = call.parameters[VERSION_CODE_KEY]

                val release = releases.let { releases ->
                    if (versionCode == null || versionCode == "last") releases.lastOrNull()
                    else releases.findLast { it.versionId == versionCode.toIntOrNull() }
                }

                release?.let {
                    call.respond(it)
                } ?: call.respondText("Unable to retrieve releases", status = HttpStatusCode.NotFound)
            }
            get("download") {
                val versionCode = call.parameters[VERSION_CODE_KEY]

                val os = try {
                    OS.valueOf(call.request.queryParameters["os"].orEmpty().uppercase())
                } catch (e: IllegalArgumentException) {
                    OS.WINDOWS
                }

                val release = releases.let { releases ->
                    if (versionCode == null || versionCode == "last") releases.lastOrNull()
                    else releases.findLast { it.versionId == versionCode.toIntOrNull() }
                }

                val path =
                    release?.paths?.firstOrNull { it.extension in os.executableFileTypes }.orEmpty()

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
                    call.respond(
                        HttpStatusCode.NotFound,
                        "No file found for requested OS (${os.name}) or for requested version (${versionCode?.toIntOrNull() ?: "last"})"
                    )
                }
            }
        }
    }
}