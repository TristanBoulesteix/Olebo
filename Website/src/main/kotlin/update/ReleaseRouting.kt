package fr.olebo.update

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.releaseRouting() {
    route("/releases") {
        get {
            call.respond(releases)
        }
        get("/last") {
            call.respond(releases.last())
        }
    }
}