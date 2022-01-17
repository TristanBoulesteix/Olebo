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
import kotlin.math.PI

@Composable
fun OleboSceneCanvas(viewModel: ShareSceneViewModel) {
    ContentCanvas(viewModel)
    CursorCanvas(viewModel)
}

@OptIn(ExperimentalComposeWebStyleApi::class)
@Composable
fun ContentCanvas(viewModel: ShareSceneViewModel) {
    val tokens = viewModel.tokens
    val backgroundImage = viewModel.background

    Canvas(
        attrs = classes(
            ShareSceneStyleSheet.oleboCanvasContainer,
            ShareSceneStyleSheet.css {
                backgroundImage(backgroundImage)
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
}

@Composable
private fun CursorCanvas(viewModel: ShareSceneViewModel) {
    val cursor = viewModel.cursor

    Canvas(
        attrs = classes(ShareSceneStyleSheet.oleboCanvasContainer),
        drawWith = { context ->
            if (cursor != null) {
                val (centerX, centerY) = cursor.position

                val (r, g, b) = cursor.color
                val (rBorder, gBorder, bBorder) = cursor.borderColor

                context.beginPath()
                context.arc(relativeX(centerX), relativeY(centerY), 15.0, 0.0, 2 * PI, false)
                context.fillStyle = "rgb($r, $g, $b)"
                context.fill()
                context.lineWidth = 2.0
                context.strokeStyle = "rgb($rBorder, $gBorder, $bBorder)"
                context.stroke()
            }
        }
    )
}