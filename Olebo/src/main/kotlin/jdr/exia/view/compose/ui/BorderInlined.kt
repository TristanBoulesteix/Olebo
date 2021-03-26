package jdr.exia.view.compose.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BorderInlined(val strokeWidth: Dp, val color: Color) {
    companion object {
        val defaultBorder = BorderInlined(2.dp, Color.Black)
    }
}

@Stable
fun Modifier.border(
    start: BorderInlined? = null,
    top: BorderInlined? = null,
    end: BorderInlined? = null,
    bottom: BorderInlined? = null,
) = drawBehind {
    start?.let {
        drawStartBorder(it, shareTop = top != null, shareBottom = bottom != null)
    }
    top?.let {
        drawTopBorder(it, shareStart = start != null, shareEnd = end != null)
    }
    end?.let {
        drawEndBorder(it, shareTop = top != null, shareBottom = bottom != null)
    }
    bottom?.let {
        drawBottomBorder(borderInlined = it, shareStart = start != null, shareEnd = end != null)
    }
}

@Stable
fun Modifier.border(border: BorderInlined) = this.border(start = border, top = border, end = border, bottom = border)

private fun DrawScope.drawTopBorder(
    borderInlined: BorderInlined,
    shareStart: Boolean = true,
    shareEnd: Boolean = true
) {
    val strokeWidthPx = borderInlined.strokeWidth.toPx()
    if (strokeWidthPx == 0f) return
    drawPath(
        Path().apply {
            moveTo(0f, 0f)
            lineTo(if (shareStart) strokeWidthPx else 0f, strokeWidthPx)
            val width = size.width
            lineTo(if (shareEnd) width - strokeWidthPx else width, strokeWidthPx)
            lineTo(width, 0f)
            close()
        },
        color = borderInlined.color
    )
}

private fun DrawScope.drawBottomBorder(
    borderInlined: BorderInlined,
    shareStart: Boolean,
    shareEnd: Boolean
) {
    val strokeWidthPx = borderInlined.strokeWidth.toPx()
    if (strokeWidthPx == 0f) return
    drawPath(
        Path().apply {
            val width = size.width
            val height = size.height
            moveTo(0f, height)
            lineTo(if (shareStart) strokeWidthPx else 0f, height - strokeWidthPx)
            lineTo(if (shareEnd) width - strokeWidthPx else width, height - strokeWidthPx)
            lineTo(width, height)
            close()
        },
        color = borderInlined.color
    )
}

private fun DrawScope.drawStartBorder(
    borderInlined: BorderInlined,
    shareTop: Boolean = true,
    shareBottom: Boolean = true
) {
    val strokeWidthPx = borderInlined.strokeWidth.toPx()
    if (strokeWidthPx == 0f) return
    drawPath(
        Path().apply {
            moveTo(0f, 0f)
            lineTo(strokeWidthPx, if (shareTop) strokeWidthPx else 0f)
            val height = size.height
            lineTo(strokeWidthPx, if (shareBottom) height - strokeWidthPx else height)
            lineTo(0f, height)
            close()
        },
        color = borderInlined.color
    )
}

private fun DrawScope.drawEndBorder(
    borderInlined: BorderInlined,
    shareTop: Boolean = true,
    shareBottom: Boolean = true
) {
    val strokeWidthPx = borderInlined.strokeWidth.toPx()
    if (strokeWidthPx == 0f) return
    drawPath(
        Path().apply {
            val width = size.width
            val height = size.height
            moveTo(width, 0f)
            lineTo(width - strokeWidthPx, if (shareTop) strokeWidthPx else 0f)
            lineTo(width - strokeWidthPx, if (shareBottom) height - strokeWidthPx else height)
            lineTo(width, height)
            close()
        },
        color = borderInlined.color
    )
}