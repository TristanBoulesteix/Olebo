package fr.olebo.sharescene

import fr.olebo.sharescene.css.MaterialStyleSheet
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposableInBody

private fun main() {
    renderComposableInBody {
        Style(MaterialStyleSheet)
        Style(ShareSceneStyleSheet)

        Div(attrs = classes(ShareSceneStyleSheet.rootContainer)) {
            ShareSceneForm()
        }
    }
}