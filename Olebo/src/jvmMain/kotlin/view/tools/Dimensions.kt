package jdr.exia.view.tools

import androidx.compose.ui.geometry.Size
import jdr.exia.model.element.SizeElement

private val Size.area
    get() = width * height

operator fun Size.compareTo(size: Size) = area.compareTo(size.area)

val maxElementSize = Size(SizeElement.XS.value.toFloat(), SizeElement.XS.value.toFloat())