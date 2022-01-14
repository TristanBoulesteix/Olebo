package fr.olebo.sharescene

import androidx.compose.runtime.*
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import fr.olebo.sharescene.websocket.client
import io.ktor.http.cio.websocket.*
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposableInBody

private fun main() {
    renderComposableInBody {
        Style(ShareSceneStyleSheet)

        var connectionState: ConnectionState by remember { mutableStateOf(Disconnected) }

        when (val state = connectionState) {
            !is Connected -> Div(attrs = classes(ShareSceneStyleSheet.rootContainer)) {
                Form(state) { connectionState = it }
            }
            else -> OleboSceneCanvas(state.shareSceneViewModel.background, state.shareSceneViewModel.tokens)
        }
    }
}

@Composable
private fun Form(connectionState: ConnectionState, setConnectionState: (ConnectionState) -> Unit) {
    ShareSceneForm(connectionState) { userName, sessionCode ->
        initWebsocket(
            client = client,
            path = "share-scene/$sessionCode?name=$userName",
            onFailure = {
                setConnectionState(Disconnected.ConnectionFailed)
                it.close()
            },
            socketBlock = { manager, setSessionCode ->
                try {
                    val connectedState = Connected(manager)

                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            when (val message = frame.getMessageOrNull()) {
                                is NewMap -> {
                                    setConnectionState(connectedState)
                                    setSessionCode(sessionCode)

                                    connectedState.shareSceneViewModel.background = message.backgroundImage
                                    connectedState.shareSceneViewModel.tokens = message.tokens
                                }
                                is TokenStateChanged -> connectedState.shareSceneViewModel.tokens = message.tokens
                                else -> continue
                            }
                        }
                    }
                } finally {
                    setConnectionState(Disconnected)
                    manager.close()
                }
            }
        )
    }
}