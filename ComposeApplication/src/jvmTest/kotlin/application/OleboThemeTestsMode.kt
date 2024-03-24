package fr.olebo.tests.application

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import fr.olebo.application.LocalThemeManager
import fr.olebo.application.OleboTheme
import fr.olebo.application.ThemeMode
import fr.olebo.application.darkColorPalette
import fr.olebo.models.SystemDarkThemeProvider
import fr.olebo.tests.applicationScope
import fr.olebo.tests.assertColorsEquals
import fr.olebo.tests.setContentWithDI
import org.kodein.di.DI
import org.kodein.di.bindProvider
import kotlin.test.*

class OleboThemeTestsMode {
    private lateinit var diDarkTheme: DI

    private lateinit var diLightTheme: DI

    @BeforeTest
    fun initialize() {
        diDarkTheme = DI { bindProvider<SystemDarkThemeProvider> { SystemDarkThemeProvider { true } } }
        diLightTheme = DI { bindProvider<SystemDarkThemeProvider> { SystemDarkThemeProvider { false } } }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `get correct palette when using dark theme`() = runComposeUiTest {
        setContentWithDI(diDarkTheme) {
            applicationScope.OleboTheme {
                val themeManager = LocalThemeManager.current
                val colors = MaterialTheme.colors

                assertColorsEquals(
                    expectedColors = colors,
                    actualColors = darkColorPalette,
                    message = { "Check color $it for dark theme" }
                )
                assertEquals(true, themeManager.isDarkTheme)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `get if dark theme is enabled`() = runComposeUiTest {
        // Check with system dark theme
        setContentWithDI(diDarkTheme) {
            applicationScope.OleboTheme {
                val themeManager = LocalThemeManager.current

                themeManager.mode = ThemeMode.Dark
                assertTrue(themeManager.isDarkTheme)

                themeManager.mode = ThemeMode.Light
                assertFalse(themeManager.isDarkTheme)

                themeManager.mode = ThemeMode.Auto
                assertTrue(themeManager.isDarkTheme)
            }
        }

        // Check auto mode with system light theme
        setContentWithDI(diLightTheme) {
            applicationScope.OleboTheme {
                val themeManager = LocalThemeManager.current

                themeManager.mode = ThemeMode.Auto
                assertFalse(themeManager.isDarkTheme)
            }
        }
    }
}