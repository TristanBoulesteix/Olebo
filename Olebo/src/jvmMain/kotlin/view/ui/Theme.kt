package jdr.exia.view.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.jthemedetecor.OsThemeDetector
import jdr.exia.model.dao.option.Preferences
import jdr.exia.model.dao.option.ThemeMode
import jdr.exia.model.tools.withSetter
import java.util.function.Consumer

@Stable
private val darkColorPalette
    get() = darkColors(
        primary = Color.White,
        primaryVariant = Color(0, 31, 153),
        secondaryVariant = Color.Black,
        secondary = Color(0, 48, 125),
        background = Color.DarkGray
    )

@Stable
private val lightColorPalette
    get() = lightColors(
        primary = Color.Black,
        primaryVariant = Color(225, 250, 249),
        secondaryVariant = Color(158, 195, 255),
        secondary = Color(255, 200, 0)
    )

private val osThemeDetector = OsThemeDetector.getDetector()

class OleboTheme(themeMode: ThemeMode) {
    var themeMode by mutableStateOf(themeMode) withSetter {
        Preferences.themeMode = it
    }
}

val LocalTheme = staticCompositionLocalOf { OleboTheme(Preferences.themeMode) }

@Composable
fun OleboTheme(content: @Composable () -> Unit) {
    val themeMode = LocalTheme.current.themeMode

    var isDarkTheme by remember(themeMode) {
        mutableStateOf(
            when (themeMode) {
                ThemeMode.Dark -> true
                ThemeMode.Light -> false
                ThemeMode.Auto -> osThemeDetector.isDark
            }
        )
    }

    if (themeMode == ThemeMode.Auto)
        DisposableEffect(Unit) {
            val listener = Consumer { state: Boolean -> isDarkTheme = state }

            osThemeDetector.registerListener(listener)

            onDispose {
                osThemeDetector.removeListener(listener)
            }
        }

    val colors = if (isDarkTheme) darkColorPalette else lightColorPalette

    MaterialTheme(
        colors = colors,
        typography = typography,
        content = content
    )
}

val isDarkTheme
    @Composable
    @ReadOnlyComposable
    get() = !MaterialTheme.colors.isLight
