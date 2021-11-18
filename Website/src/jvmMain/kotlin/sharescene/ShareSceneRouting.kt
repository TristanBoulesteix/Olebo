package fr.olebo.sharescene

import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*

private const val PARAM_NAME = "sceneId"

fun Routing.shareSceneRouting() {
    webSocket("scene/{$PARAM_NAME}") {
        val sceneId = call.parameters[PARAM_NAME]!!
        send(Frame.Text("Connected with id $sceneId"))
    }
}