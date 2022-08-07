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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import jdr.exia.model.dao.option.SerializableLabelState
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Element
import jdr.exia.view.tools.*
import jdr.exia.viewModel.MasterViewModel
import org.jetbrains.skia.*
import kotlin.math.abs
import org.jetbrains.skia.Rect as SkiaRect

@Composable
fun ComposeMapPanel(modifier: Modifier, viewModel: MasterViewModel, isMasterWindow: Boolean = true) = Box(modifier) {
    Image(
        bitmap = viewModel.backgroundImage,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )

    val focusManager = LocalFocusManager.current

    var selectedArea: Rect? by remember { mutableStateOf(null) }

    var moveOffset: Offset? by remember { mutableStateOf(null) }

    var startMouseOffset by remember { mutableStateOf(Offset.Zero) }

    key(viewModel.commandManager.composeKey) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .applyIf(isMasterWindow) {
                    Modifier
                        .onMouseEvents { eventType ->
                            when (eventType) {
                                PointerEventType.Release -> {
                                    onMouseReleased(
                                        viewModel = viewModel,
                                        selectedArea = selectedArea,
                                        moveOffset = moveOffset,
                                        resetMoveOffset = { moveOffset = null },
                                        startMouseOffset = startMouseOffset,
                                        resetSelectedArea = { selectedArea = null }
                                    )
                                }

                                PointerEventType.Press -> if (isMasterWindow) focusManager.clearFocus()
                                PointerEventType.Move -> viewModel.setCursor(if (event.keyboardModifiers.isAltPressed) null else mouseOffset.absoluteOffset)
                                PointerEventType.Exit -> viewModel.setCursor(null)
                            }
                        }
                        .onMouseDrag { start, end ->
                            if (startPressButtons.isPrimaryPressed) {
                                if (viewModel.hasElementAtPosition(start.absoluteOffset)) {
                                    moveOffset = end
                                    selectedArea = null
                                } else {
                                    moveOffset = null
                                    selectedArea = Rect(
                                        Offset(start.x.coerceAtMost(end.x), start.y.coerceAtMost(end.y)),
                                        Size(abs(start.x - end.x), abs(start.y - end.y))
                                    )
                                }

                                startMouseOffset = start
                            }
                        }
                }
        ) {
            val labelColor = Settings.labelColor.contentColor
            val labelState = Settings.labelState

            viewModel.elements.forEach { element ->
                if (isMasterWindow || element.isVisible) {
                    drawIntoCanvas {
                        // For better performances, we use native canvas to draw tokens
                        it.nativeCanvas.drawImageRect(
                            Image.makeFromBitmap(element.spriteBitmap.asSkiaBitmap()),
                            SkiaRect.makeXYWH(
                                element.referenceOffset.x.relativeX(size),
                                element.referenceOffset.y.relativeY(size),
                                element.hitBox.width.toFloat().relativeX(size),
                                element.hitBox.height.toFloat().relativeY(size)
                            )
                        )
                    }
                }

                if ((isMasterWindow && labelState.isVisible) || labelState == SerializableLabelState.FOR_BOTH) {
                    drawLabel(element, labelColor)
                }

                if (isMasterWindow && !element.isVisible) {
                    drawRectangleAroundToken(element, Color.Blue, 3)
                }
            }

            if (isMasterWindow) {
                // Draw selected marker
                viewModel.selectedElements.forEach {
                    drawRectangleAroundToken(it, Color.Red, 4)
                }
            }

            if (!isMasterWindow && Settings.cursorEnabled) {
                drawCursor(viewModel.cursor)
            }
        }
    }

    key(selectedArea) {
        Canvas(Modifier.fillMaxSize()) {
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
    moveOffset: Offset?,
    resetMoveOffset: () -> Unit,
    startMouseOffset: Offset,
    resetSelectedArea: () -> Unit
) {
    when {
        startPressButtons.isPrimaryPressed -> if (moveOffset == null && selectedArea == null) viewModel.selectElementsAtPosition(
            mouseOffset.absoluteOffset,
            event.keyboardModifiers.isCtrlPressed
        )

        startPressButtons.isSecondaryPressed || startPressButtons.isTertiaryPressed -> viewModel.moveTokensTo(
            mouseOffset.absoluteOffset
        )
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

    moveOffset?.absoluteOffset?.let {
        viewModel.moveTokensTo(it, startMouseOffset.absoluteOffset)
        resetMoveOffset()
    }
}

private fun DrawScope.drawCursor(cursor: Offset?) {
    if (cursor != null) {
        val (cursorColor, borderCursorColor) = Settings.cursorColor

        drawCircle(
            color = cursorColor,
            center = Offset(cursor.x.relativeX(size), cursor.y.relativeY(size)),
            radius = 15f
        )

        drawCircle(
            color = borderCursorColor,
            center = Offset(cursor.x.relativeX(size), cursor.y.relativeY(size)),
            radius = 15f,
            style = Stroke(3f)
        )
    }
}

private fun DrawScope.drawLabel(token: Element, labelColor: Color) {
    val (refX, refY) = token.referenceOffset

    val font = Font(Typeface.makeFromName("Arial", FontStyle.BOLD), 20f)
    val paint = Paint().apply {
        color = labelColor
    }.asFrameworkPaint()
    val textLine = TextLine.make(token.alias, font)

    drawIntoCanvas {
        it.nativeCanvas.drawTextLine(
            textLine,
            refX.relativeX(size) + (token.hitBox.width.toFloat().relativeX(size) - textLine.width) / 2,
            refY.relativeY(size) - 10,
            paint
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
