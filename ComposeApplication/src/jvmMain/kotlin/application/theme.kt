package fr.olebo.application

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import fr.olebo.models.SystemDarkThemeProvider
import org.kodein.di.compose.rememberInstance

enum class ThemeMode {
    Dark, Light, Auto;
}

val LocalThemeManager = staticCompositionLocalOf<ThemeManager> { error("No theme manager available") }

@Stable
interface ThemeManager {
    var mode: ThemeMode

    val isDarkTheme: Boolean
}

@Composable
fun ApplicationScope.OleboTheme(content: ApplicationContent) {
    val checkSystemDarkTheme: SystemDarkThemeProvider by rememberInstance()

    val isSystemDarkTheme = checkSystemDarkTheme()

    val themeManager = remember {
        object : ThemeManager {
            override var mode by mutableStateOf(ThemeMode.Auto)

            override val isDarkTheme: Boolean
                get() = when (mode) {
                    ThemeMode.Dark -> true
                    ThemeMode.Light -> false
                    ThemeMode.Auto -> isSystemDarkTheme
                }
        }
    }

    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        MaterialTheme(
            colors = if (themeManager.isDarkTheme) darkColorPalette else lightColorPalette,
            typography = typography,
            content = { content() }
        )
    }
}