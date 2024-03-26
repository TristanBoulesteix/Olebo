package fr.olebo.application

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.Notification

@Stable
interface TrayManager {
    var trayHint: String

    fun sendNotification(notification: Notification)
}

val LocalTrayManager = staticCompositionLocalOf<TrayManager> {
    throw IllegalStateException("Cannot access Tray before application is initialized.")
}

object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
    }
}