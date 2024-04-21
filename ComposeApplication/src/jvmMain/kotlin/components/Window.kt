package fr.olebo.components

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import java.awt.Dimension
import java.awt.Window

val LocalWindowInfo = staticCompositionLocalOf<OleboWindowInfo?> { null }

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

        val parentWindow = LocalWindowInfo.current

        CompositionLocalProvider(LocalWindowInfo provides remember { OleboWindowInfoImpl(parentWindow, window) }) {
            content()
        }
    }
}

@Immutable
sealed interface OleboWindowInfo {
    val parentWindow: OleboWindowInfo?

    val awtWindow: Window

    fun addSettingsChangedListener(action: () -> Unit)

    fun triggerSettingsChanged()
}

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

internal fun DpSize.toAwtDimension() = Dimension(width.value.toInt(), height.value.toInt())