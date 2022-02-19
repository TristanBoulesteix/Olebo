package jdr.exia.view.ui

import androidx.compose.material.Colors
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
val Colors.backgroundImageColor
    get() = if (!isLight) Color(54, 54, 54) else Color(245, 245, 245)