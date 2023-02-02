package jdr.exia.view.ui

import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import jdr.exia.DeveloperModeManager
import jdr.exia.update.UpdateTrayIcon

typealias ApplicationContent = @Composable ApplicationScope.() -> Unit

@Stable
interface TrayManager {
    var trayHint: String

    fun sendNotification(notification: Notification)
}

val LocalTrayManager = staticCompositionLocalOf<TrayManager> {
    throw IllegalStateException("Cannot access Tray before application is initialized.")
}

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

@Composable
private fun ApplicationScope.Tray(trayState: TrayState, text: String) {
    Tray(icon = UpdateTrayIcon, state = trayState, tooltip = text)

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