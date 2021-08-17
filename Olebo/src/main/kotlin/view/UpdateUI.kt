package jdr.exia.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.rememberNotification
import androidx.compose.ui.window.rememberTrayState
import jdr.exia.localization.STR_UPDATE_AVAILABLE
import jdr.exia.localization.ST_NEW_VERSION_AVAILABLE
import jdr.exia.localization.StringLocale
import jdr.exia.update.Release
import jdr.exia.view.tools.showMessage

@Composable
fun UpdateUI(release: Release) {
    TrayUpdate()
}

@Composable
private fun TrayUpdate() {
    val trayState = rememberTrayState()
    val notification = rememberNotification(
        title = StringLocale[STR_UPDATE_AVAILABLE],
        message = StringLocale[ST_NEW_VERSION_AVAILABLE],
        type = Notification.Type.Info
    )

    Tray(
        state = trayState,
        icon = TrayIcon,
        menu = {
            Item("Hi", onClick = {})
        },
        onAction = {
            showMessage("Hi")
        }
    )

    LaunchedEffect(Unit) {
        trayState.sendNotification(notification)
    }
}

private object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
    }
}