package fr.olebo.sharescene

import androidx.compose.runtime.Composable
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.backgroundFitImage
import fr.olebo.sharescene.css.classes
import org.jetbrains.compose.web.ExperimentalComposeWebStyleApi
import org.jetbrains.compose.web.css.backgroundSize
import org.jetbrains.compose.web.dom.Div

@OptIn(ExperimentalComposeWebStyleApi::class)
@Composable
fun OleboSceneCanvas(
    background: Base64Image,
    tokens: List<Token>
) = Div(
    attrs = classes(
        ShareSceneStyleSheet.oleboCanvasContainer,
        ShareSceneStyleSheet.css {
            backgroundFitImage(background)
            backgroundSize("cover")
        }
    )
) {

}