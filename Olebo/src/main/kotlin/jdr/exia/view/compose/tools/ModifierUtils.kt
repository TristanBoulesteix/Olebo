package jdr.exia.view.compose.tools

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Apply modification to [Modifier] only if condition is true
 */
@Stable
inline fun Modifier.applyIf(condition: Boolean, mod: Modifier.() -> Modifier) =
    if (condition) this.mod() else this

@Immutable
data class BorderBuilder(val strokeWidth: Dp, val color: Color) {
    companion object {
        @Stable
        val defaultBorder = BorderBuilder(2.dp, Color.Black)
    }
}

@Stable
fun Modifier.border(
    start: BorderBuilder? = null,
    top: BorderBuilder? = null,
    end: BorderBuilder? = null,
    bottom: BorderBuilder? = null,
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
        drawBottomBorder(borderBuilder = it, shareStart = start != null, shareEnd = end != null)
    }
}

@Stable
fun Modifier.border(border: BorderBuilder) = this.border(start = border, top = border, end = border, bottom = border)

private fun DrawScope.drawTopBorder(
    borderBuilder: BorderBuilder,
    shareStart: Boolean = true,
    shareEnd: Boolean = true
) {
    val strokeWidthPx = borderBuilder.strokeWidth.toPx()
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
        color = borderBuilder.color
    )
}

private fun DrawScope.drawBottomBorder(
    borderBuilder: BorderBuilder,
    shareStart: Boolean,
    shareEnd: Boolean
) {
    val strokeWidthPx = borderBuilder.strokeWidth.toPx()
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
        color = borderBuilder.color
    )
}

private fun DrawScope.drawStartBorder(
    borderBuilder: BorderBuilder,
    shareTop: Boolean = true,
    shareBottom: Boolean = true
) {
    val strokeWidthPx = borderBuilder.strokeWidth.toPx()
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
        color = borderBuilder.color
    )
}

private fun DrawScope.drawEndBorder(
    borderBuilder: BorderBuilder,
    shareTop: Boolean = true,
    shareBottom: Boolean = true
) {
    val strokeWidthPx = borderBuilder.strokeWidth.toPx()
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
        color = borderBuilder.color
    )
}