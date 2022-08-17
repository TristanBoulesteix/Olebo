package fr.olebo.sharescene

import fr.olebo.sharescene.connection.ConnectionError
import fr.olebo.sharescene.connection.getConnectionError
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.utils.io.core.*

class ShareSceneManager internal constructor(
    private val client: HttpClient,
    private val serverURL: URL,
    private val path: String,
    private val socketBlock: suspend DefaultClientWebSocketSession.(manager: ShareSceneManager, setSessionCode: (String) -> Unit) -> Unit,
    private val onFailure: (cause: ConnectionError) -> Unit
) : Closeable {
    var codeSession: String? = null
        private set

    val sceneUrl
        get() = codeSession?.let {
            val port = serverURL.security.port
            val portUrl = if(port != 443) ":$port" else ""
            "${serverURL.security.value}://${serverURL.domain}$portUrl/$path/$it"
        }

    internal val viewModel = ShareSceneViewModel()

    internal suspend fun initWebsocket() {
        val manager = this

        try {
            if(serverURL.security == UrlProtocol.HTTPS) {
                client.wss(
                    host = serverURL.domain,
                    port = serverURL.security.port,
                    path = path
                ) {
                    socketBlock(manager) { codeSession = it }
                }
            } else {
                client.webSocket(
                    host = serverURL.domain,
                    port = serverURL.security.port,
                    path = path
                ) {
                    socketBlock(manager) { codeSession = it }
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            onFailure(t.getConnectionError())
        }
    }

    override fun close() {
        client.close()
        viewModel.close()
    }
}