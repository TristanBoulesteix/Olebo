package fr.olebo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import java.awt.Dimension
import java.awt.Window

/**
 * CompositionLocal to hold window information
 */
val LocalWindowInfo = staticCompositionLocalOf<OleboWindowInfo?> { null }

/**
 * Composable function to create a window within an application scope.
 *
 * @param title The title of the window.
 * @param size The initial size of the window.
 * @param minimumSize The minimum size of the window (optional).
 * @param placement The placement of the window (default is WindowPlacement.Floating).
 * @param content The composable content to be displayed within the window.
 */
@Composable
fun ApplicationScope.Window(
    title: String,
    size: DpSize,
    minimumSize: DpSize? = null,
    placement: WindowPlacement = WindowPlacement.Floating,
    content: @Composable FrameWindowScope.() -> Unit
) {
    // Remember the state of the window, including size, position, and placement
    val windowState = rememberWindowState(
        size = size,
        position = WindowPosition(Alignment.Center),
        placement = placement
    )

    // Create the window with the specified properties
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = title,
        focusable = true
    ) {
        // Set the minimum size of the window if provided
        LaunchedEffect(minimumSize) {
            minimumSize?.let {
                window.minimumSize = it.toAwtDimension()
            }
        }

        // Set the preferred size of the window
        LaunchedEffect(size) {
            window.preferredSize = size.toAwtDimension()
        }

        // Get the parent window information from the composition local
        val parentWindow = LocalWindowInfo.current

        // Provide the window information to the composition local and display the content
        CompositionLocalProvider(LocalWindowInfo provides remember { OleboWindowInfoImpl(parentWindow, window) }) {
            Box(Modifier.fillMaxSize().background(color = MaterialTheme.colors.background)) {
                content()
            }
        }
    }
}

/**
 * Interface representing window information.
 */
@Immutable
sealed interface OleboWindowInfo {
    val parentWindow: OleboWindowInfo?

    val awtWindow: Window

    /**
     * Adds a listener for settings changes.
     *
     * @param action The action to be performed when settings change.
     */
    fun addSettingsChangedListener(action: () -> Unit)

    /**
     * Triggers the settings changed event.
     */
    fun triggerSettingsChanged()
}

/**
 * Implementation of the OleboWindowInfo interface.
 *
 * @property parentWindow The parent window information.
 * @property awtWindow The AWT [Window] instance.
 */
@Immutable
private class OleboWindowInfoImpl(
    override val parentWindow: OleboWindowInfo?,
    override val awtWindow: Window
) : OleboWindowInfo {
    private val observers = mutableListOf<() -> Unit>()

    override fun addSettingsChangedListener(action: () -> Unit) {
        observers.add(action)
    }

    override fun triggerSettingsChanged() {
        observers.forEach { it.invoke() }
    }
}

/**
 * Extension function to convert [DpSize] to AWT [Dimension].
 *
 * @return The corresponding [Dimension] object.
 */
internal fun DpSize.toAwtDimension() = Dimension(width.value.toInt(), height.value.toInt())