@file:Suppress("FunctionName")

package jdr.exia.view.compose.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val darkColorPalette = darkColors(
    primary = black,
    primaryVariant = lightBlue,
    secondary = lightOrange
)

private val lightColorPalette = lightColors(
    primary = black,
    primaryVariant = lightBlue,
    secondary = lightOrange
)

@Composable
fun OleboTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        darkColorPalette
    } else {
        lightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        content = content
    )
}