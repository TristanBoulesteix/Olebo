package fr.olebo.sharescene

import androidx.compose.runtime.Composable
import fr.olebo.sharescene.components.Canvas
import fr.olebo.sharescene.components.relativeX
import fr.olebo.sharescene.components.relativeY
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.backgroundImage
import fr.olebo.sharescene.css.classes
import org.jetbrains.compose.web.ExperimentalComposeWebStyleApi
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.backgroundSize
import org.w3c.dom.Image

@OptIn(ExperimentalComposeWebStyleApi::class)
@Composable
fun OleboSceneCanvas(
    background: Base64Image,
    tokens: List<Token>
) = Canvas(
    attrs = classes(
        ShareSceneStyleSheet.oleboCanvasContainer,
        ShareSceneStyleSheet.css {
            backgroundImage(background)
            backgroundSize("100% 100%")
            backgroundRepeat("no-repeat")
        }
    ),
    drawWith = { context ->
        tokens.forEach {
            Image().apply {
                val (tokenX, tokenY) = it.position

                onload = { _ ->
                    context.drawImage(
                        this,
                        relativeX(tokenX),
                        relativeY(tokenY),
                        relativeX(it.size),
                        relativeY(it.size)
                    )
                }

                src = it.image.cssBase64ImageCode
            }
        }
    }
)