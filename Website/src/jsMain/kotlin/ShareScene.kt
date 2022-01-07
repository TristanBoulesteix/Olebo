package fr.olebo.sharescene

import androidx.compose.runtime.*
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import fr.olebo.sharescene.websocket.client
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposableInBody

private fun main() {
    renderComposableInBody {
        Style(ShareSceneStyleSheet)

        var connectionState: ConnectionState by remember { mutableStateOf(Disconnected) }

        if (connectionState !is Connected)
            Div(attrs = classes(ShareSceneStyleSheet.rootContainer)) {
                Form { connectionState = it }
            }
    }
}

@Composable
private fun Form(setConnectionState: (ConnectionState) -> Unit) {
    ShareSceneForm { userName, sessionCode ->
        initWebsocket(
            client = client,
            path = "share-scene/$sessionCode?name=$userName",
            onFailure = {
                setConnectionState(Disconnected.ConnectionFailed)
                it.close()
            },
            socketBlock = { manager, setSessionCode ->
                try {
                    setConnectionState(Connected(manager))
                    setSessionCode(sessionCode)

                    for (frame in incoming) {
                        println(frame)
                    }
                } finally {
                    setConnectionState(Disconnected)
                    manager.close()
                }
            }
        )
    }
}