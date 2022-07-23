package jdr.exia.view.composable.master

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Element
import jdr.exia.view.tools.*
import jdr.exia.viewModel.MasterViewModel
import org.jetbrains.skia.*
import kotlin.math.abs

@Composable
fun ComposeMapPanel(modifier: Modifier, viewModel: MasterViewModel) = Box(modifier) {
    Image(
        bitmap = viewModel.backgroundImage.toComposeImageBitmap(),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )

    var selectedArea: Rect? by remember { mutableStateOf(null) }

    Canvas(
        modifier = Modifier.fillMaxSize().onMouseEvents { eventType ->
            if (eventType == PointerEventType.Release) {
                onMouseReleased(viewModel, selectedArea) { selectedArea = null }
            }
        }.onMouseDrag { start, end ->
            if (buttons.isPrimaryPressed) {
                selectedArea = if (viewModel.hasElementAtPosition(start.absoluteOffset)) null else Rect(
                    Offset(start.x.coerceAtMost(end.x), start.y.coerceAtMost(end.y)),
                    Size(abs(start.x - end.x), abs(start.y - end.y))
                )
            }
        }
    ) {
        // Draw selected marker
        viewModel.selectedElements.forEach {
            drawRectangleAroundToken(it, Color.Red, 4)
        }

        val labelColor = Settings.labelColor.contentColor
        val labelState = Settings.labelState

        viewModel.elements.forEach { element ->
            drawImage(
                image = element.sprite.toComposeImageBitmap(),
                dstOffset = IntOffset(
                    element.referenceOffset.x.relativeX(size).toInt(),
                    element.referenceOffset.y.relativeY(size).toInt()
                ),
                dstSize = IntSize(
                    element.hitBox.width.toFloat().relativeX(size).toInt(),
                    element.hitBox.height.toFloat().relativeY(size).toInt()
                )
            )

            if (labelState.isVisible) {
                drawLabel(element, labelColor)
            }

            if (!element.isVisible) {
                drawRectangleAroundToken(element, Color.Blue, 3)
            }

            selectedArea?.let { area ->
                drawRect(Color.Red, area.topLeft, area.size, style = Stroke(2.dp.toPx()))
                drawRect(Color.Red, area.topLeft, area.size, alpha = 0.05f)
            }
        }
    }
}

@Stable
private fun EventHandler.onMouseReleased(
    viewModel: MasterViewModel,
    selectedArea: Rect?,
    resetSelectedArea: () -> Unit
) {
    when {
        buttons.isPrimaryPressed -> if (selectedArea == null) viewModel.selectElementsAtPosition(
            mouseOffset.absoluteOffset,
            event.keyboardModifiers.isCtrlPressed
        )
        buttons.isSecondaryPressed || buttons.isTertiaryPressed -> viewModel.moveTokensTo(mouseOffset.absoluteOffset)
    }

    selectedArea?.let {
        if (it.size >= maxElementSize) {
            viewModel.selectElements(it) { token ->
                Rect(
                    Offset(
                        token.referenceOffset.x.relativeX(componentAreaSize),
                        token.referenceOffset.y.relativeY(componentAreaSize)
                    ),
                    Size(
                        token.hitBox.width.toFloat().relativeX(componentAreaSize),
                        token.hitBox.height.toFloat().relativeY(componentAreaSize)
                    )
                )
            }
        }

        resetSelectedArea()
    }
}

private fun DrawScope.drawLabel(token: Element, labelColor: Color) {
    val (refX, refY) = token.referenceOffset

    val font = Font(Typeface.makeFromName("Arial", FontStyle.BOLD), 20f)
    val text = token.alias

    drawIntoCanvas {
        it.nativeCanvas.drawTextLine(
            TextLine.make(text, font),
            refX.relativeX(size) + (token.hitBox.width.toFloat().relativeX(size) - font.measureTextWidth(text)) / 2,
            refY.relativeY(size) - 10,
            Paint().apply {
                color = labelColor.toArgb()
            }
        )
    }
}

/**
 * Draw a rectangle around a given token.
 *
 * @param token The token to draw around
 * @param color The color of the rectangle outline
 * @param referentialSize An integer used as base to know at which distance drawing the rectangle
 */
private fun DrawScope.drawRectangleAroundToken(token: Element, color: Color, referentialSize: Int) {
    drawRect(
        color = color,
        topLeft = Offset(
            token.referenceOffset.x.relativeX(size) - referentialSize,
            token.referenceOffset.y.relativeY(size) - referentialSize
        ),
        size = Size(
            token.hitBox.width.toFloat().relativeX(size) + referentialSize * 2,
            token.hitBox.height.toFloat().relativeY(size) + referentialSize * 2
        ),
        style = Stroke(4.dp.toPx())
    )
}

/**
 * Translates an X coordinate in 1600:900px to proportional coords according to this window's size
 *
 * @receiver The unit to convert as a [Float]
 * @param size The size to use as reference
 *
 * @return A relative [Float]
 */
@Stable
private fun Float.relativeX(size: Size): Float =
    (this * size.width) / MasterViewModel.ABSOLUTE_WIDTH.toInt()

/**
 * Translates a y coordinate in 1600:900px to proportional coords according to this window's size
 *
 * @receiver The unit to convert as a [Float]
 * @param size The size to use as reference
 *
 * @return A relative [Float]
 */
@Stable
private fun Float.relativeY(size: Size): Float =
    (this * size.height) / MasterViewModel.ABSOLUTE_HEIGHT.toInt()
