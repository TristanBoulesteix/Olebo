package fr.olebo.log

import io.ktor.server.websocket.*

val WebSocketServerSession.log
    inline get() = application.environment.log