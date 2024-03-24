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

    @get:Composable
    @get:ReadOnlyComposable
    val isDarkTheme: Boolean
}

@Stable
private class ThemeManagerImpl(private val isSystemDarkTheme: SystemDarkThemeProvider) : ThemeManager {
    override var mode by mutableStateOf(ThemeMode.Auto)

    override val isDarkTheme: Boolean
        @Composable
        get() = when (mode) {
            ThemeMode.Dark -> true
            ThemeMode.Light -> false
            ThemeMode.Auto -> isSystemDarkTheme()
        }
}

@Composable
fun ApplicationScope.OleboTheme(content: ApplicationContent) {
    val isSystemDarkTheme: SystemDarkThemeProvider by rememberInstance()

    val themeManager = remember { ThemeManagerImpl(isSystemDarkTheme) }

    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        MaterialTheme(
            colors = if (themeManager.isDarkTheme) darkColorPalette else lightColorPalette,
            typography = typography,
            content = { content() }
        )
    }
}