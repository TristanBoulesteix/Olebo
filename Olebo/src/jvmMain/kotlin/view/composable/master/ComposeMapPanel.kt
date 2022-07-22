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
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import jdr.exia.model.element.Element
import jdr.exia.view.tools.EventHandler
import jdr.exia.view.tools.onMouseEvent
import jdr.exia.viewModel.MasterViewModel

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
        viewModel.selectedElements.forEach {
            drawSelectedMarker(it)
        }

        viewModel.elements.forEach {
            drawImage(
                image = it.sprite.toComposeImageBitmap(),
                dstOffset = IntOffset(relativeX(it.referenceOffset.x).toInt(), relativeY(it.referenceOffset.y).toInt()),
                dstSize = IntSize(
                    relativeX(it.hitBox.width.toFloat()).toInt(),
                    relativeY(it.hitBox.height.toFloat()).toInt()
                )
            )
        }
    }
}

@Stable
private fun EventHandler.onMouseReleased(viewModel: MasterViewModel) {
    // Add receiver instead of a lot of params
    when {
        buttons.isPrimaryPressed -> viewModel.selectElementsAtPosition(
            mouseOffset.absoluteOffset,
            event.keyboardModifiers.isCtrlPressed
        )
        buttons.isSecondaryPressed || buttons.isTertiaryPressed -> viewModel.moveTokensTo(mouseOffset.absoluteOffset)
    }
}

private fun DrawScope.drawSelectedMarker(token: Element) {
    drawRect(
        color = Color.Red,
        topLeft = Offset(relativeX(token.referenceOffset.x) - 4f, relativeY(token.referenceOffset.y) - 4f),
        size = Size(relativeX(token.hitBox.width.toFloat()) + 8, relativeY(token.hitBox.height.toFloat()) + 8),
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
