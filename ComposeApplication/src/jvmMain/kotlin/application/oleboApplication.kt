package fr.olebo.application

import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import fr.olebo.application.style.OleboTheme
import fr.olebo.domain.coroutine.ApplicationIoScope
import fr.olebo.resources.Res
import fr.olebo.resources.olebo_is_running
import kotlinx.coroutines.cancel
import org.jetbrains.compose.resources.getString
import org.kodein.di.DI
import org.kodein.di.compose.withDI
import org.kodein.di.direct
import org.kodein.di.instance


/**
 * A type alias for a composable function that operates within an ApplicationScope.
 * This composable function represents the content of an application.
 */
typealias ApplicationContent = @Composable ApplicationScope.() -> Unit


/**
 * Sets up and starts the Olebo application with Dependency Injection ([DI]) and theming.
 *
 * @param di the dependency injection container.
 * @param content the main application content to be displayed.
 */
fun oleboApplication(di: DI, content: ApplicationContent) = application(exitProcessOnExit = false) {
    WithDI(di) {
        OleboTheme {
            val trayManager = remember (::TrayManagerImpl)
            val viewModelStoreOwner = remember (::CompositionScopedViewModelStoreOwner)

            CompositionLocalProvider(
                LocalTrayManager provides trayManager,
                LocalViewModelStoreOwner provides viewModelStoreOwner
            ) {
                content()

                LaunchedEffect(Unit) {
                    trayManager.trayHint = getString(Res.string.olebo_is_running)
                }

                Tray(icon = TrayIcon, state = trayManager.trayState, tooltip = trayManager.trayHint)
            }
        }
    }
}

/**
 * Sets up dependency injection ([DI]) within a composable scope and manages the lifecycle of the application IO scope.
 *
 * @param di a dependency injection container used to provide dependencies.
 * @param content the main application content to be displayed within the DI context.
 */
@Composable
fun ApplicationScope.WithDI(di: DI, content: ApplicationContent) {
    withDI(di) {
        content()
    }

    DisposableEffect(Unit) {
        onDispose {
            di.direct.instance<ApplicationIoScope>().cancel()
        }
    }
}

/**
 * Implementation of the [TrayManager] interface for managing tray notifications and hints.
 *
 * This class is marked as Stable for Compose stability checks and uses MutableState to
 * manage and observe state changes in the tray hint.
 *
 * @property trayState Maintains the state of the system tray.
 */
@Stable
private class TrayManagerImpl : TrayManager {
    val trayState = TrayState()

    override var trayHint by mutableStateOf("")

    override fun sendNotification(notification: Notification) = trayState.sendNotification(notification)
}

/**
 * A composition-local to provide instances of [ViewModelStoreOwner] in a composable function.
 * This is used to scope [ViewModel] instances to the composition.
 *
 * **See Also:** [GitHub](https://gist.github.com/manuelvicnt/a2e4c4812243ac1b218b24d0ac8d22bb)
 */
private class CompositionScopedViewModelStoreOwner : ViewModelStoreOwner, RememberObserver {

    override val viewModelStore = ViewModelStore()

    override fun onAbandoned() {
        viewModelStore.clear()
    }

    override fun onForgotten() {
        viewModelStore.clear()
    }

    override fun onRemembered() = Unit
}