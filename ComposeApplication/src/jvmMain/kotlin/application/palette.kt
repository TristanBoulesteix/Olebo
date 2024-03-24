package fr.olebo.application

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
val darkColorPalette
    get() = darkColors(
        primary = Color.White,
        primaryVariant = Color(0, 31, 153),
        secondaryVariant = Color.Black,
        secondary = Color(0, 48, 125),
        background = Color.DarkGray
    )

@Stable
val lightColorPalette
    get() = lightColors(
        primary = Color.Black,
        primaryVariant = Color(225, 250, 249),
        secondaryVariant = Color(158, 195, 255),
        secondary = Color(255, 200, 0)
    )