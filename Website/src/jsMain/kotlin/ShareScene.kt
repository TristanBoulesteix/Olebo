package fr.olebo.sharescene

import androidx.compose.runtime.*
import fr.olebo.sharescene.connection.Connected
import fr.olebo.sharescene.connection.ConnectionState
import fr.olebo.sharescene.connection.Disconnected
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import fr.olebo.sharescene.websocket.client
import io.ktor.websocket.*
import jdr.exia.localization.StringLocale
import jdr.exia.localization.getBrowserLanguage
import jdr.exia.localization.invoke
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposableInBody

private fun main() {
    StringLocale(::getBrowserLanguage)

    renderComposableInBody {
        Style(ShareSceneStyleSheet)

        var connectionState: ConnectionState by remember { mutableStateOf(Disconnected) }

        when (val state = connectionState) {
            !is Connected -> Div(attrs = classes(ShareSceneStyleSheet.rootContainer)) {
                LaunchedEffect(Unit) {
                    document.title = "ShareScene login"
                }
                Form({ connectionState }) {
                    connectionState = it
                }
            }
            else -> {
                LaunchedEffect(Unit) {
                    document.title = "ShareScene"
                }
                OleboSceneCanvas(state.shareSceneViewModel)
            }
        }
    }
}

@Composable
private fun Form(getConnectionState: () -> ConnectionState, setConnectionState: (ConnectionState) -> Unit) {
    val connectionStateProvider by rememberUpdatedState(getConnectionState)

    ShareSceneForm(connectionStateProvider(), setConnectionState) { userName, sessionCode ->
        val location = window.location

        initWebsocket(
            client = client,
            serverAddress = URL("${location.protocol}//${location.hostname}"),
            path = "share-scene/$sessionCode?name=$userName",
            onFailure = { connectionError ->
                setConnectionState(Disconnected.ConnectionFailed(connectionError))
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
                                is ConnectionRefused -> setConnectionState(Disconnected.ConnectionFailed())
                                is CursorHidden -> connectedState.shareSceneViewModel.cursor = null
                                is CursorMoved -> connectedState.shareSceneViewModel.cursor = message.cursor
                                null, is NewSessionCreated, is PlayerAddedOrRemoved -> continue
                            }
                        }
                    }
                } finally {
                    if (connectionStateProvider() !is Disconnected)
                        setConnectionState(Disconnected)
                    manager.close()
                    //window.location.replace("https://olebo.fr/share-scene/")
                }
            }
        )
    }
}