package fr.olebo.sharescene

import fr.olebo.sharescene.css.OleboStyleSheet
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposableInBody

private fun main() {
    renderComposableInBody {
        Style(OleboStyleSheet)
        Style(ShareSceneStyleSheet)

        Div(attrs = classes(ShareSceneStyleSheet.rootContainer)) {
            ShareSceneForm()
        }
    }
}