package fr.olebo.log

import io.ktor.websocket.*

val WebSocketServerSession.log
    inline get() = application.environment.log