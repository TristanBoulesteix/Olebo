package fr.olebo.sharescene

import fr.olebo.sharescene.connection.ConnectionError
import fr.olebo.sharescene.connection.getConnectionError
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.utils.io.core.*

class ShareSceneManager internal constructor(
    private val client: HttpClient,
    private val path: String,
    private val socketBlock: suspend DefaultClientWebSocketSession.(manager: ShareSceneManager, setSessionCode: (String) -> Unit) -> Unit,
    private val onFailure: (cause: ConnectionError) -> Unit
) : Closeable {
    var codeSession: String? = null
        private set

    val sceneUrl
        get() = codeSession?.let { "http://localhost:8080/share-scene/$it" }

    internal val viewModel = ShareSceneViewModel()

    internal suspend fun initWebsocket() {
        val manager = this

        try {
            client.webSocket(host = "localhost", port = 8080, path = path) {
                socketBlock(manager) { codeSession = it }
            }
        } catch (t: Throwable) {
            onFailure(t.getConnectionError())
        }
    }

    override fun close() {
        client.close()
        viewModel.close()
    }
}