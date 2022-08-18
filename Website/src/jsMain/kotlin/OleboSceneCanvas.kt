package fr.olebo.sharescene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import fr.olebo.sharescene.components.Canvas
import fr.olebo.sharescene.components.relativePosition
import fr.olebo.sharescene.components.relativeX
import fr.olebo.sharescene.components.relativeY
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.backgroundImage
import fr.olebo.sharescene.css.classes
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.backgroundSize
import org.w3c.dom.CENTER
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.Image
import kotlin.math.PI

@Composable
fun OleboSceneCanvas(viewModel: ShareSceneViewModel) {
    ContentCanvas(viewModel)
    CursorCanvas(viewModel)
}

@Composable
fun ContentCanvas(viewModel: ShareSceneViewModel) {
    val tokens = viewModel.tokens
    val backgroundImage = viewModel.background

    key(tokens) {
        Canvas(
            attrs = {
                classes(ShareSceneStyleSheet.oleboCanvasContainer)
                style {
                    backgroundImage(backgroundImage)
                    backgroundSize("100% 100%")
                    backgroundRepeat("no-repeat")
                }
            },
            draw = {
                tokens.forEach {
                    Image().apply {
                        val (tokenX, tokenY) = relativePosition(it.position)
                        val tokenWidth = relativeX(it.size)
                        val tokenHeight = relativeY(it.size)

                        // Draw image of token
                        onload = { _ ->
                            translate(tokenX + width / 2.0, tokenY + height / 2.0)
                            rotate(it.rotation.radians)
                            translate(-width / 2.0, -height / 2.0)

                            drawImage(
                                this,
                                0.0,
                                0.0,
                                if (it.rotation.isOnSide) tokenWidth else tokenHeight,
                                if (it.rotation.isOnSide) tokenHeight else tokenWidth
                            )

                            setTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)
                        }

                        src = it.image.cssBase64ImageCode

                        // Draw label of token
                        it.label?.let { label ->
                            val (r, g, b) = label.color

                            font = "bold 24px Arial sans-serif"
                            fillStyle = "rgb($r, $g, $b)"
                            textAlign = CanvasTextAlign.CENTER

                            fillText(
                                label.text,
                                relativeX(tokenX) + relativeX(it.size) / 2,
                                relativeY(tokenY) - 10
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun CursorCanvas(viewModel: ShareSceneViewModel) {
    val cursor = viewModel.cursor

    Canvas(
        attrs = classes(ShareSceneStyleSheet.oleboCanvasContainer),
        draw = {
            if (cursor != null) {
                val (centerX, centerY) = cursor.position

                val (r, g, b) = cursor.color
                val (rBorder, gBorder, bBorder) = cursor.borderColor

                beginPath()
                arc(relativeX(centerX), relativeY(centerY), 15.0, 0.0, 2 * PI, false)
                fillStyle = "rgb($r, $g, $b)"
                fill()
                lineWidth = 2.0
                strokeStyle = "rgb($rBorder, $gBorder, $bBorder)"
                stroke()
            }
        }
    )
}