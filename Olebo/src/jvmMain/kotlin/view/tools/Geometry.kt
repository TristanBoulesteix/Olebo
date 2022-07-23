package jdr.exia.view.tools

import androidx.compose.ui.geometry.Rect

operator fun Rect.contains(rect: Rect): Boolean =
    contains(rect.topLeft) && contains(rect.topRight) && contains(rect.bottomLeft) && contains(rect.bottomRight)