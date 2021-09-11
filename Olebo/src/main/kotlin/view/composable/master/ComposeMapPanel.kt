package jdr.exia.view.composable.master

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toComposeBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import jdr.exia.viewModel.MasterViewModel

@Composable
fun ComposeMapPanel(modifier: Modifier, viewModel: MasterViewModel) = Box(modifier) {
    Image(
        bitmap = viewModel.backGroundImage.toComposeBitmap(),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        viewModel.elements.forEach {
            drawImage(
                image = it.sprite.toComposeBitmap(),
                srcOffset = IntOffset(relativeX(it.referencePoint.x), relativeY(it.referencePoint.y)),
                srcSize = IntSize(relativeX(it.hitBox.width), relativeY(it.hitBox.height))
            )

        }
    }
}

/**
 * Translates an X coordinate in 1600:900px to proportional coords according to this window's size
 */
private fun DrawScope.relativeX(absoluteX: Int): Int {
    return (absoluteX * this.size.width).toInt() / MasterViewModel.ABSOLUTE_WIDTH
}

/**
 * Translates a y coordinate in 1600:900px to proportional coords according to this window's size
 */
private fun DrawScope.relativeY(absoluteY: Int): Int {
    return (absoluteY * this.size.height).toInt() / MasterViewModel.ABSOLUTE_HEIGHT
}