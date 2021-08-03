package jdr.exia.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.*
import jdr.exia.view.ui.defaultCursor
import jdr.exia.view.ui.handCursor
import java.awt.Dimension
import java.util.*
import javax.swing.JFrame
import javax.swing.JMenuBar

object WindowManager {
    val composeWindows = mutableListOf<SwingState>()

    val currentFocusedState
        get() = composeWindows.find { it.window.isFocused }
}

class SwingState(val window: ComposeWindow) {
    private val id = UUID.randomUUID()

    private var hoverRequestCount = 0
        set(value) {
            field = value

            if (field < 0) field = 0

            window.cursor = if (field > 0) handCursor else defaultCursor
        }

    fun hasItemhovered(requestHover: Boolean) {
        if (requestHover) hoverRequestCount++ else hoverRequestCount--
    }

    fun hasSwingItemHovered() {
        hoverRequestCount = 0
    }

    override fun equals(other: Any?) = other is SwingState && id == other.id

    override fun hashCode() = id.hashCode()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApplicationScope.Window(
    title: String = "", size: WindowSize, minimumSize: WindowSize? = null, isVisible: Boolean = true,
    menuBar: JMenuBar, content: @Composable () -> Unit
) {
    val windowState = rememberWindowState(
        size = size,
        position = WindowPosition(Alignment.Center)
    )

    if (isVisible) {
        Window(onCloseRequest = ::exitApplication, state = windowState, title = title) {
            LaunchedEffect(Unit) {
                minimumSize?.let { (width, height) ->
                    window.minimumSize = Dimension(width.value.toInt(), height.value.toInt())
                }

                window.jMenuBar = menuBar
            }

            DisposableEffect(Unit) {
                val swingState = SwingState(window)

                WindowManager.composeWindows += swingState

                onDispose {
                    WindowManager.composeWindows -= swingState
                    println("dispose")
                }
            }

            content()
        }
    }
}

abstract class ComposableWindow(title: String = "") : JFrame(title) {
    companion object {
        private val composableWindows = mutableListOf<ComposableWindow>()

        val currentFocused
            get() = composableWindows.find { it.isFocused }
    }

    private var hoverRequestCount = 0
        set(value) {
            field = value

            if (field < 0) field = 0

            cursor = if (field > 0) handCursor else defaultCursor
        }

    fun hasItemhovered(requestHover: Boolean) {
        if (requestHover) hoverRequestCount++ else hoverRequestCount--
    }

    fun hasSwingItemHovered() {
        hoverRequestCount = 0
    }

    override fun setVisible(isVisible: Boolean) {
        if (isVisible) {
            composableWindows += this
        } else {
            composableWindows -= this
        }
        super.setVisible(isVisible)
    }

    override fun dispose() {
        composableWindows -= this
        super.dispose()
    }
}