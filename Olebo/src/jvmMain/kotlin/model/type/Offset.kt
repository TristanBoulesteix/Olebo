package jdr.exia.model.type

import androidx.compose.ui.geometry.Offset
import java.awt.Rectangle

operator fun Offset.contains(rectangle: Rectangle) = rectangle.contains(x.toDouble(), y.toDouble())