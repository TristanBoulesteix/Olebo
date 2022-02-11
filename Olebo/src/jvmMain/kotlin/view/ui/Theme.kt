package jdr.exia.view.ui

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.jthemedetecor.OsThemeDetector

@Stable
private val darkColorPalette
    get() = darkColors(
        primary = Color.White,
        primaryVariant = Color(0, 31, 153),
        secondaryVariant = Color.Black,
        secondary = Color(176, 88, 0),
        background = Color.DarkGray,
        onSurface = Color.LightGray
    )

@Stable
private val lightColorPalette
    get() = lightColors(
        primary = Color.Black,
        primaryVariant = Color(225, 250, 249),
        secondaryVariant = Color(158, 195, 255),
        secondary = Color(255, 200, 0),
        onSurface = Color.White
    )

@Stable
private val osThemeDetector = OsThemeDetector.getDetector()

@Composable
fun OleboTheme(content: @Composable () -> Unit) {
    var isDarkTheme by remember { mutableStateOf(osThemeDetector.isDark) }

    LaunchedEffect(Unit) {
        osThemeDetector.registerListener {
            isDarkTheme = it
        }
    }

    val colors = if (isDarkTheme) {
        darkColorPalette
        lightColorPalette
    } else {
        lightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        content = {
            CompositionLocalProvider(LocalScrollbarStyle provides defaultScrollbarStyle()) {
                content()
            }
        }
    )
}


val isDarkTheme
    @Composable
    @ReadOnlyComposable
    get() = !MaterialTheme.colors.isLight
