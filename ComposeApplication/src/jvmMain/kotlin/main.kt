@file:JvmName("Olebo")

package fr.olebo

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope
import fr.olebo.application.oleboApplication
import fr.olebo.domain.navigation.HomeScreen
import fr.olebo.domain.navigation.NavigationHandler
import fr.olebo.domain.navigation.ScenarioScreen
import fr.olebo.features.scenario.play.GameMasterWindow
import fr.olebo.features.startup.StartupWindow
import org.kodein.di.compose.rememberInstance

internal const val OLEBO_VERSION_NAME = "0.2.0"

/**
 * This code must be unique between releases and must be incremented for each one
 */
internal const val OLEBO_VERSION_CODE = 10

internal fun main() = oleboApplication(injector) {
    Application()
}

@Composable
internal fun ApplicationScope.Application() {
    val navigationHandler by rememberInstance<NavigationHandler>()

    when (val screen = navigationHandler.currentScreen) {
        is HomeScreen -> StartupWindow()
        is ScenarioScreen -> GameMasterWindow(screen.scenarioInfo)
    }
}