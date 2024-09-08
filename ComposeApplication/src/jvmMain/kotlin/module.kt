package fr.olebo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.olebo.application.style.SystemDarkThemeProvider
import fr.olebo.domain.adaptors.system.OsAdaptor
import fr.olebo.domain.domainModule
import fr.olebo.domain.models.ConfigurationItem
import fr.olebo.domain.models.OleboConfiguration
import fr.olebo.domain.navigation.HomeScreen
import fr.olebo.domain.navigation.NavigationHandler
import fr.olebo.domain.navigation.Route
import fr.olebo.memory.memoryModule
import fr.olebo.persistence.persistenceModule
import fr.olebo.system.systemModule
import org.kodein.di.*
import java.io.File

private val module by DI.Module {
    bindSingleton("olebo-directory") {
        "${instance<OsAdaptor>().current.appDataDir}${File.separator}Olebo${File.separator}"
    }
    bindSet<ConfigurationItem> {
        add { provider { OleboConfiguration(OLEBO_VERSION_NAME, OLEBO_VERSION_CODE) } }
    }
    bindProvider { SystemDarkThemeProvider { isSystemInDarkTheme() } }
    bindSingletonOf(::NavigationHandler)
}

@Stable
val injector = DI {
    import(module)
    import(domainModule)
    import(systemModule)
    import(persistenceModule)
    import(memoryModule)
}

/**
 * Creates an instance of [NavigationHandler] that maintains the current screen state.
 *
 * This function returns an implementation of the [NavigationHandler] interface with
 * a mutable state of the `currentScreen`, initially set to `HomeScreen`.
 * The `navigate` method can be used to update the `currentScreen`.
 */
private fun NavigationHandler() = object : NavigationHandler {
    override var currentScreen by mutableStateOf<Route>(HomeScreen)

    override fun navigate(screen: Route) {
        currentScreen = screen
    }
}