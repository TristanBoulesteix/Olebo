@file:Suppress("FunctionName")

package jdr.exia.view.compose.components

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.WindowEvents
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.MenuBar
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferedImage
import javax.swing.JMenuBar
import javax.swing.SwingUtilities

fun CustomWindow(
    title: String = "",
    size: IntSize = IntSize(800, 600),
    minimumSize: IntSize? = null,
    location: IntOffset = IntOffset.Zero,
    centered: Boolean = true,
    icon: BufferedImage? = null,
    menuBar: MenuBar? = null,
    jMenuBar: JMenuBar? = null,
    undecorated: Boolean = false,
    resizable: Boolean = true,
    events: WindowEvents = WindowEvents(),
    onDismissRequest: (() -> Unit)? = null,
    content: @Composable () -> Unit = { },
) = SwingUtilities.invokeLater {
    AppWindow(
        title = title,
        size = size,
        location = location,
        centered = centered,
        icon = icon,
        menuBar = menuBar,
        undecorated = undecorated,
        resizable = resizable,
        events = events,
        onDismissRequest = onDismissRequest
    ).apply {
        minimumSize?.let { (width, height) ->
            this.window.minimumSize = Dimension(width, height)
        }

        if (menuBar == null && jMenuBar != null) {
            this.window.addWindowFocusListener(object : WindowAdapter() {
                override fun windowGainedFocus(event: WindowEvent) {
                    if (invoker == null) {
                        window.jMenuBar = jMenuBar
                    }
                }
            })
        }
    }.show {
        content()
    }
}