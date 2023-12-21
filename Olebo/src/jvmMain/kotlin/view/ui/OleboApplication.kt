package jdr.exia.view.ui

import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import jdr.exia.DeveloperModeManager
import jdr.exia.localization.STR_QUIT_OLEBO
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get
import jdr.exia.update.UpdateTrayIcon
import org.slf4j.LoggerFactory

typealias ApplicationContent = @Composable ApplicationScope.() -> Unit

/**
 * Represents a tray manager for displaying notifications.
 *
 * This interface provides methods for setting the tray hint and sending notifications.
 * Implementations of this interface should provide the necessary functionality to interact with the system tray.
 *
 */
@Stable
interface TrayManager {
    var trayHint: String

    fun sendNotification(notification: Notification)
}

/**
 * Represents a composition local value for accessing the TrayManager.
 *
 * The LocalTrayManager is used to provide access to the TrayManager instance.
 * It is created as a composition local value using the staticCompositionLocalOf function.
 *
 * The TrayManager instance can only be accessed after the application is initialized.
 * If accessed before initialization, an IllegalStateException will be thrown.
 *
 * Usage:
 * ```
 * val trayManager: TrayManager = LocalTrayManager.current
 * ```
 *
 * @throws IllegalStateException if accessed before application initialization
 */
val LocalTrayManager = staticCompositionLocalOf<TrayManager> {
    throw IllegalStateException("Cannot access Tray before application is initialized.")
}

val LocalLogger = staticCompositionLocalOf { LoggerFactory.getLogger("Olebo")!! }

/**
 * Launches the Olebo application with the provided content.
 *
 * @param content The content to be displayed in the application.
 */
fun oleboApplication(content: ApplicationContent) = application(exitProcessOnExit = false) {
    val trayManager = remember {
        object : TrayManager {
            val trayState = TrayState()

            override var trayHint by mutableStateOf("")

            override fun sendNotification(notification: Notification) = trayState.sendNotification(notification)
        }
    }

    OleboTheme {
        CompositionLocalProvider(LocalTrayManager provides trayManager) {
            content()
        }

        Tray(trayManager.trayState, trayManager.trayHint)
    }
}

/**
 * Displays a tray notification using the provided `trayState` and `text`.
 *
 * @param trayState The state of the tray notification.
 * @param text The text to be displayed in the tray notification tooltip.
 */
@Composable
private fun ApplicationScope.Tray(trayState: TrayState, text: String) {
    Tray(icon = UpdateTrayIcon, state = trayState, tooltip = text){
        Item(StringLocale[STR_QUIT_OLEBO], onClick = ::exitApplication)
    }

    LaunchedEffect(Unit) {
        DeveloperModeManager.enabledFlow.collect { isEnabled ->
            trayState.sendNotification(
                Notification(
                    if (isEnabled) "Mode développeur activé !" else "Mode développeur désactivé",
                    "",
                    Notification.Type.Info
                )
            )
        }
    }
}