package jdr.exia.view.composable.master

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import jdr.exia.view.tools.EventHandler
import jdr.exia.view.tools.onMouseEvent
import jdr.exia.viewModel.MasterViewModel
import org.jetbrains.skia.*

@Composable
fun ComposeMapPanel(modifier: Modifier, viewModel: MasterViewModel) = Box(modifier) {
    Image(
        bitmap = viewModel.backgroundImage.toComposeImageBitmap(),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )

    Canvas(
        modifier = Modifier.fillMaxSize().onMouseEvent(PointerEventType.Release) {
            onMouseReleased(viewModel)
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
                    relativeX(element.referenceOffset.x).toInt(),
                    relativeY(element.referenceOffset.y).toInt()
                ),
                dstSize = IntSize(
                    relativeX(element.hitBox.width.toFloat()).toInt(),
                    relativeY(element.hitBox.height.toFloat()).toInt()
                )
            )

            if (labelState.isVisible) {
                drawLabel(element, labelColor)
            }

            if (!element.isVisible) {
                drawRectangleAroundToken(element, Color.Blue, 3)
            }
        }
    }
}

@Stable
private fun EventHandler.onMouseReleased(viewModel: MasterViewModel) {
    when {
        buttons.isPrimaryPressed -> viewModel.selectElementsAtPosition(
            mouseOffset.absoluteOffset,
            event.keyboardModifiers.isCtrlPressed
        )
        buttons.isSecondaryPressed || buttons.isTertiaryPressed -> viewModel.moveTokensTo(mouseOffset.absoluteOffset)
    }
}

private fun DrawScope.drawLabel(token: Element, labelColor: Color) {
    val (refX, refY) = token.referenceOffset

    val font = Font(Typeface.makeFromName("Arial", FontStyle.BOLD), 20f)
    val text = token.alias

    drawIntoCanvas {
        it.nativeCanvas.drawTextLine(
            TextLine.make(text, font),
            relativeX(refX) + (relativeX(token.hitBox.width.toFloat()) - font.measureTextWidth(text)) / 2,
            relativeY(refY) - 10,
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
            relativeX(token.referenceOffset.x) - referentialSize,
            relativeY(token.referenceOffset.y) - referentialSize
        ),
        size = Size(
            relativeX(token.hitBox.width.toFloat()) + referentialSize * 2,
            relativeY(token.hitBox.height.toFloat()) + referentialSize * 2
        ),
        style = Stroke(4.dp.toPx())
    )
}

/**
 * Translates an X coordinate in 1600:900px to proportional coords according to this window's size
 */
@Stable
private fun DrawScope.relativeX(absoluteX: Float): Float =
    (absoluteX * this.size.width) / MasterViewModel.ABSOLUTE_WIDTH.toInt()

/**
 * Translates a y coordinate in 1600:900px to proportional coords according to this window's size
 */
@Stable
private fun DrawScope.relativeY(absoluteY: Float): Float =
    (absoluteY * this.size.height) / MasterViewModel.ABSOLUTE_HEIGHT.toInt()
