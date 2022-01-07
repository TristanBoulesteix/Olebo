package fr.olebo.sharescene

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.utils.io.core.*

class ShareSceneManager internal constructor(
    private val client: HttpClient,
    private val path: String,
    private val socketBlock: suspend DefaultClientWebSocketSession.(manager: ShareSceneManager, setSessionCode: (String) -> Unit) -> Unit,
    private val onFailure: (manager: ShareSceneManager) -> Unit
) : Closeable by client {
    private var codeSession: String? = null

    val sceneUrl
        get() = codeSession?.let { "localhost/share-scene/$it" }

    internal suspend fun initWebsocket() {
        val manager = this

        try {
            client.webSocket(host = "localhost", port = 8080, path = path) {
                socketBlock(manager) { codeSession = it }
            }
        } catch (t: Throwable) {
            onFailure(manager)
        }
    }

    override fun close() {
        client.close()
        println("client closed")
    }
}