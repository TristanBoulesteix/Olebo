package fr.olebo.application.style

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
val darkColorPalette
    get() = darkColors(
        primary = Color.LightGray,
        primaryVariant = Color(0, 31, 153),
        secondaryVariant = Color.Black,
        secondary = Color(0, 48, 125)
    )

@Stable
val lightColorPalette
    get() = lightColors(
        primary = Color.Black,
        primaryVariant = Color(225, 250, 249),
        secondaryVariant = Color(158, 195, 255),
        secondary = Color(255, 200, 0)
    )