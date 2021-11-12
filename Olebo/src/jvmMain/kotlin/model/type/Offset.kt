package jdr.exia.model.type

import androidx.compose.ui.geometry.Offset
import java.awt.Point
import java.awt.Rectangle

fun Offset(point: Point) = Offset(point.x.toFloat(), point.y.toFloat())

operator fun Offset.contains(rectangle: Rectangle) = rectangle.contains(x.toDouble(), y.toDouble())