package jdr.exia.view.composable.master

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalDesktopApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import jdr.exia.model.dao.option.SerializableColor
import jdr.exia.model.dao.option.SerializableLabelState
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Element
import jdr.exia.viewModel.MasterViewModel.Companion.ABSOLUTE_HEIGHT
import jdr.exia.viewModel.MasterViewModel.Companion.ABSOLUTE_WIDTH
import org.jetbrains.skija.*
import org.jetbrains.skija.Color as SkijaColor

@OptIn(ExperimentalDesktopApi::class)
@Composable
fun MapPanel(
    modifier: Modifier,
    isParentMaster: Boolean,
    background: ImageBitmap,
    tokens: List<Element>,
    selectedElement: List<Element>
) {
    var selectedArea: Rect? by remember { mutableStateOf(null) }

    Canvas(modifier = modifier.drawWithCache {
        onDrawBehind {
            drawImage(image = background, dstSize = IntSize(size.width.toInt(), size.height.toInt()))
        }
    }.pointerInput(Unit) {
        var start = Offset(0f, 0f)

        detectDragGestures(
            onDragStart = {
                start = it
            },
            onDrag = { change, _ ->
                change.consumeAllChanges()
                selectedArea = Rect(
                    topLeft = start,
                    bottomRight = change.position
                )
            },
            onDragEnd = {
                selectedArea = null
            }
        )
    }) {
        tokens.forEach { token ->
            if (isParentMaster || token.isVisible) {
                if (!token.isVisible) {
                    drawMarker(
                        color = Color.Blue,
                        coordinatesDiff = 3,
                        sizeDiff = 6,
                        token = token
                    )
                }

                if (selectedElement.isNotEmpty() && token in selectedElement) {
                    drawMarker(
                        color = Color.Red,
                        coordinatesDiff = 4,
                        sizeDiff = 8,
                        token = token
                    )
                }

                val labelState = Settings.labelState

                if ((isParentMaster && labelState.isVisible) || labelState == SerializableLabelState.FOR_BOTH)
                    drawLabel(token = token, labelColor = Settings.labelColor)

                // draw the element
                drawImage(
                    image = token.spriteBitmap,
                    dstOffset = IntOffset(
                        relativeX(token.referencePoint.x).toInt(),
                        relativeY(token.referencePoint.y).toInt()
                    ),
                    dstSize = IntSize(relativeX(token.hitBox.width).toInt(), relativeY(token.hitBox.height).toInt())
                )

                selectedArea?.let {
                    drawRect(
                        color = Color.Red,
                        topLeft = it.topLeft,
                        size = it.size,
                        style = Stroke(width = .5f)
                    )

                    drawRect(
                        color = Color.Red.copy(alpha = .2f),
                        topLeft = it.topLeft,
                        size = it.size,
                        blendMode = BlendMode.Lighten
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawMarker(
    color: Color,
    coordinatesDiff: Int,
    sizeDiff: Int,
    token: Element
) = drawRect(
    color = color,
    topLeft = Offset(
        relativeX(token.referencePoint.x) - coordinatesDiff,
        relativeY(token.referencePoint.y) - coordinatesDiff
    ),
    size = Size(relativeX(token.hitBox.width) + sizeDiff, relativeY(token.hitBox.height) + sizeDiff),
    style = Stroke(width = .5f)
)

private fun DrawScope.drawLabel(token: Element, labelColor: SerializableColor) {
    val (refX, refY) = token.referencePoint

    val font = Font(Typeface.makeFromName("Arial", FontStyle.BOLD)).apply {
        this.size = 24F
    }

    val line = TextLine.make(token.alias, font)

    val paint = Paint().apply {
        color = SkijaColor.makeARGB(
            labelColor.contentColor.alpha,
            labelColor.contentColor.red,
            labelColor.contentColor.green,
            labelColor.contentColor.blue
        )
    }

    val x = relativeX(refX) + (relativeX(token.hitBox.width) - line.width) / 2
    val y = relativeY(refY) - 10

    drawIntoCanvas {
        it.nativeCanvas.drawTextLine(line, x, y, paint)
    }
}

/**
 * Translates an X coordinate in 1600:900px to proportional coords according to this window's size
 */
private fun DrawScope.relativeX(absoluteX: Int) = (absoluteX * this.size.width) / ABSOLUTE_WIDTH

/**
 * Translates a y coordinate in 1600:900px to proportional coords according to this window's size
 */
private fun DrawScope.relativeY(absoluteY: Int) = (absoluteY * this.size.height) / ABSOLUTE_HEIGHT