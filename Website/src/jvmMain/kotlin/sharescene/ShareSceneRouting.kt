package fr.olebo.sharescene

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*

fun Routing.shareSceneRouting() {
    route("scene") {
        get {
            call.respondText("Hello World!")
        }
        webSocket("echo") {
            send(Frame.Text("Enter your name"))
        }
    }
}