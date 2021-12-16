package jdr.exia.service

import fr.olebo.sharescene.NewSessionCreated
import fr.olebo.sharescene.getMessageOrNull
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import java.io.Closeable

class ShareSceneManager private constructor(private val client: HttpClient) : Closeable by client {
    private var codeSession: String? = null

    val sceneUrl
        get() = codeSession?.let { "localhost/share-scene/$it" }

    suspend fun initWebsocket(onConnected: () -> Unit, onDisconnected: () -> Unit, onFailure: () -> Unit) {
        try {
            client.webSocket(host = "localhost", port = 8080, path = "share-scene") {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> when (val message = frame.getMessageOrNull()) {
                            is NewSessionCreated -> {
                                codeSession = message.code
                                onConnected()
                            }
                            else -> Unit
                        }
                        else -> Unit
                    }
                }

                onDisconnected()
            }
        } catch (t: Throwable) {
            onFailure()
        }
    }

    companion object {
        operator fun invoke() = ShareSceneManager(socketClient)
    }
}