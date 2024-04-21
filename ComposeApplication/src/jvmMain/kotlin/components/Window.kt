package fr.olebo.components

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import java.awt.Dimension
import java.awt.Window

val LocalWindow = staticCompositionLocalOf<OleboWindowStatus?> { null }

@Composable
fun ApplicationScope.Window(
    title: String,
    size: DpSize,
    minimumSize: DpSize? = null,
    placement: WindowPlacement = WindowPlacement.Floating,
    content: @Composable FrameWindowScope.() -> Unit
) {
    val windowState = rememberWindowState(
        size = size,
        position = WindowPosition(Alignment.Center),
        placement = placement
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = title,
        focusable = true
    ) {
        LaunchedEffect(minimumSize) {
            minimumSize?.let {
                window.minimumSize = it.toAwtDimension()
            }
        }

        LaunchedEffect(size) {
            window.preferredSize = size.toAwtDimension()
        }

        val parentWindow = LocalWindow.current

        CompositionLocalProvider(LocalWindow provides remember { OleboWindowStatusImpl(parentWindow, window) }) {
            content()
        }
    }
}

@Immutable
sealed interface OleboWindowStatus {
    val parentWindow: OleboWindowStatus?

    val awtWindow: Window

    fun addSettingsChangedListener(action: () -> Unit)

    fun triggerSettingsChanged()
}

@Immutable
private class OleboWindowStatusImpl(
    override val parentWindow: OleboWindowStatus?,
    override val awtWindow: Window
) : OleboWindowStatus {
    private val observers = mutableListOf<() -> Unit>()

    override fun addSettingsChangedListener(action: () -> Unit) {
        observers.add(action)
    }

    override fun triggerSettingsChanged() {
        observers.forEach { it.invoke() }
    }
}

internal fun DpSize.toAwtDimension() = Dimension(width.value.toInt(), height.value.toInt())