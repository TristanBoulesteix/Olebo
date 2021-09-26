package jdr.exia.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.*
import java.awt.Dimension

object WindowStateManager {
    val composeWindows = mutableListOf<ComposeWindow>()

    val currentFocusedWindow
        get() = composeWindows.lastOrNull()
}

@Composable
fun ApplicationScope.Window(
    title: String,
    size: WindowSize,
    minimumSize: WindowSize? = null,
    placement: WindowPlacement = WindowPlacement.Floating,
    content: @Composable FrameWindowScope.() -> Unit
) {
    val windowState = rememberWindowState(
        size = size,
        position = WindowPosition(Alignment.Center),
        placement = placement
    )

    Window(onCloseRequest = ::exitApplication, state = windowState, title = title, focusable = true) {
        LaunchedEffect(minimumSize) {
            minimumSize?.let { (width, height) ->
                window.minimumSize = Dimension(width.value.toInt(), height.value.toInt())
            }
        }

        DisposableEffect(Unit) {
            WindowStateManager.composeWindows += window

            onDispose {
                WindowStateManager.composeWindows -= window
            }
        }

        content()
    }
}