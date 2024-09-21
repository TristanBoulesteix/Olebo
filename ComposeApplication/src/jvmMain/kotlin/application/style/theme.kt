package fr.olebo.application.style

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import fr.olebo.application.ApplicationContent
import fr.olebo.domain.adaptors.memory.ThemeStorageAdaptor
import fr.olebo.domain.models.application.ThemeMode
import fr.olebo.resources.Res
import fr.olebo.resources.auto_theme_label
import fr.olebo.resources.dark_theme_label
import fr.olebo.resources.light_theme_label
import org.kodein.di.compose.rememberInstance

/**
 * A composition local that provides access to the current `ThemeManager`.
 *
 * This allows composables to access and manipulate theme settings such as
 * the current theme mode and whether the dark theme is active.
 *
 * Usage of `LocalThemeManager` can be seen in theme-related composables
 * where the `ThemeManager` is required to apply and observe theme changes.
 *
 * @see ThemeManager
 * @see CompositionLocalProvider
 */
val LocalThemeManager = staticCompositionLocalOf<ThemeManager> { error("No theme manager available") }

/**
 * Provides a localized label string for different theme modes.
 *
 * This property returns a string resource based on the current theme mode.
 * It supports three modes: Auto, Dark, and Light, each corresponding to a different label.
 *
 * - `ThemeMode.Auto` maps to `Res.string.auto_theme_label`
 * - `ThemeMode.Dark` maps to `Res.string.dark_theme_label`
 * - `ThemeMode.Light` maps to `Res.string.light_theme_label`
 */
val ThemeMode.label
    get() = when (this) {
        ThemeMode.Auto -> Res.string.auto_theme_label
        ThemeMode.Dark -> Res.string.dark_theme_label
        ThemeMode.Light -> Res.string.light_theme_label
    }

/**
 * Interface for managing theme settings within the application.
 */
@Stable
interface ThemeManager {
    /**
     * The current theme mode used by the application. It can be one of the following:
     * - `ThemeMode.Dark`: Forces the application to use the dark theme.
     * - `ThemeMode.Light`: Forces the application to use the light theme.
     * - `ThemeMode.Auto`: Automatically selects the theme based on the system's settings.
     */
    var mode: ThemeMode

    /**
     * A read-only composable property that indicates if the current theme is set to dark mode.
     *
     * This property reflects the theme mode defined in the `ThemeManager` interface which includes
     * `ThemeMode.Dark`, `ThemeMode.Light`, and `ThemeMode.Auto`. In `Auto` mode, it follows the system's
     * dark theme setting specified by `SystemDarkThemeProvider`.
     *
     * @return `true` if the current theme is dark, `false` otherwise.
     */
    @get:Composable
    @get:ReadOnlyComposable
    val isDarkTheme: Boolean
}


/**
 * Interface to provide a system dark theme check.
 * This is primarily used to determine if the system-wide dark theme is enabled.
 */
internal fun interface SystemDarkThemeProvider {
    /**
     * Determines whether the system dark theme is currently enabled.
     *
     * @return true if the system dark theme is enabled, false otherwise.
     */
    @Composable
    @ReadOnlyComposable
    operator fun invoke(): Boolean
}

/**
 * Implementation of the ThemeManager interface for managing theme modes.
 *
 * @property isSystemDarkTheme A provider to determine if the system theme is dark.
 * @property themeStorageAdaptor An adaptor for storing and retrieving the current theme mode.
 */
@Stable
private class ThemeManagerImpl(
    private val isSystemDarkTheme: SystemDarkThemeProvider,
    private val themeStorageAdaptor: ThemeStorageAdaptor
) : ThemeManager {
    /**
     * Holds the current theme mode of the application.
     *
     * This state value is initialized with the current theme retrieved from the
     * `ThemeStorageAdaptor`. It can be observed and modified to update the UI
     * according to the selected theme mode (Dark, Light, or Auto).
     * Changing this state will also update the `currentTheme` in the `ThemeStorageAdaptor`.
     */
    private val modeState = mutableStateOf(themeStorageAdaptor.currentTheme)

    /**
     * The mode for theming, which can be one of the values from the ThemeMode enumeration.
     * It determines whether the application uses a light theme, dark theme, or follows the system setting.
     *
     * This property is backed by `modeState`, which stores the current theme mode.
     * Setting this property updates both `modeState` and the `currentTheme` property of `themeStorageAdaptor`.
     */
    override var mode
        get() = modeState.value
        set(value) {
            modeState.value = value
            themeStorageAdaptor.currentTheme = value
        }

    /**
     * Determines whether the current theme is dark. The value is computed based on the current theme mode.
     * - If the mode is [ThemeMode.Dark], it returns true.
     * - If the mode is [ThemeMode.Light], it returns false.
     * - If the mode is [ThemeMode.Auto], it uses the [isSystemDarkTheme] provider to determine if the system theme is dark.
     */
    override val isDarkTheme: Boolean
        @Composable
        get() = when (mode) {
            ThemeMode.Dark -> true
            ThemeMode.Light -> false
            ThemeMode.Auto -> isSystemDarkTheme()
        }
}

/**
 * Applies the Olebo theme to the provided application content.
 *
 * This composable function is responsible for setting up theming using the MaterialTheme
 * with the appropriate color palette based on the current theme mode and system preferences.
 * It also provides the [ThemeManager] to the composition.
 *
 * @param content The composable content to be displayed within the custom Olebo theme.
 */
@Composable
fun ApplicationScope.OleboTheme(content: ApplicationContent) {
    val isSystemDarkTheme: SystemDarkThemeProvider by rememberInstance()
    val themeStorageAdaptor: ThemeStorageAdaptor by rememberInstance()

    val themeManager: ThemeManager = remember { ThemeManagerImpl(isSystemDarkTheme, themeStorageAdaptor) }

    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        MaterialTheme(
            colors = if (themeManager.isDarkTheme) darkColorPalette else lightColorPalette,
            typography = typography,
            content = { content() }
        )
    }
}