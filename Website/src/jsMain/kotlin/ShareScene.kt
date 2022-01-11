package fr.olebo.sharescene

import androidx.compose.runtime.*
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import fr.olebo.sharescene.websocket.client
import io.ktor.http.cio.websocket.*
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.renderComposableInBody

var image: String? by mutableStateOf(null)

private fun main() {
    renderComposableInBody {
        Style(ShareSceneStyleSheet)

        var connectionState: ConnectionState by remember { mutableStateOf(Disconnected) }

        if (connectionState !is Connected) {
            Div(attrs = classes(ShareSceneStyleSheet.rootContainer)) {
                Form { connectionState = it }
            }
        } else {
            if (image != null)
                Img(src = "data:image/jpeg;base64,$image")
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
                        if (frame is Frame.Text) {
                            val a = frame.getMessageOrNull() as BackgroundChanged
                            image = a.value
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