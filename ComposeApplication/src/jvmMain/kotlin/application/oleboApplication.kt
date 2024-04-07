package fr.olebo.application

import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import fr.olebo.application.style.OleboTheme
import fr.olebo.domain.coroutine.ApplicationIoScope
import kotlinx.coroutines.cancel
import olebo.composeapplication.generated.resources.Res
import olebo.composeapplication.generated.resources.olebo_is_running
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.kodein.di.DI
import org.kodein.di.compose.withDI
import org.kodein.di.direct
import org.kodein.di.instance

typealias ApplicationContent = @Composable ApplicationScope.() -> Unit

@OptIn(ExperimentalResourceApi::class)
fun oleboApplication(di: DI, content: ApplicationContent) = application(exitProcessOnExit = false) {
    withDI(di) {
        OleboTheme {
            val trayManager = remember { TrayManagerImpl() }

            CompositionLocalProvider(LocalTrayManager provides trayManager) {
                content()

                LaunchedEffect(Unit) {
                    trayManager.trayHint = getString(Res.string.olebo_is_running)
                }

                Tray(icon = TrayIcon, state = trayManager.trayState, tooltip = trayManager.trayHint)
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