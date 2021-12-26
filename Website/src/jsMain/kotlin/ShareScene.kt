package fr.olebo.sharescene

import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import fr.olebo.sharescene.websocket.start
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposableInBody

private fun main() {
    renderComposableInBody {
        Style(ShareSceneStyleSheet)

        Div(attrs = classes(ShareSceneStyleSheet.rootContainer)) {
            ShareSceneForm(::connect)
        }
    }
}

private suspend fun connect(userName: String, sessionCode: String) = start(sessionCode)