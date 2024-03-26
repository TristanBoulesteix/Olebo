package fr.olebo.application

import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import fr.olebo.application.ui.OleboTheme
import fr.olebo.domain.coroutine.ApplicationIoScope
import fr.olebo.injector
import kotlinx.coroutines.cancel
import org.kodein.di.compose.withDI
import org.kodein.di.direct
import org.kodein.di.instance

typealias ApplicationContent = @Composable ApplicationScope.() -> Unit

fun oleboApplication(content: ApplicationContent) = application(exitProcessOnExit = false) {
    val di = remember { injector }

    withDI(di) {
        OleboTheme {
            val trayState = remember { TrayManagerImpl() }

            CompositionLocalProvider(LocalTrayManager provides trayState) {
                content()

                Tray(icon = TrayIcon, state = trayState.trayState, tooltip = trayState.trayHint)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            di.direct.instance<ApplicationIoScope>().cancel()
        }
    }
}

@Stable
private class TrayManagerImpl : TrayManager {
    val trayState = TrayState()

    override var trayHint by mutableStateOf("")

    override fun sendNotification(notification: Notification) = trayState.sendNotification(notification)
}