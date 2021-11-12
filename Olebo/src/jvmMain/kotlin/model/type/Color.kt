package jdr.exia.model.type

import androidx.compose.ui.graphics.Color

typealias JColor = java.awt.Color

fun Color.toJColor() = JColor(red, green, blue, alpha)

fun JColor.toColor() = Color(rgb)