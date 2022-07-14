package jdr.exia.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import java.awt.Dimension

object WindowStateManager {
    val composeWindowScopes = mutableListOf<OleboWindowScope>()

    val currentFocusedWindowScope
        get() = composeWindowScopes.lastOrNull()
}

interface OleboWindowScope : FrameWindowScope {
    fun addSettingsChangedListener(action: () -> Unit)

    fun triggerSettingsChange()
}

private class OleboWindowScopeImpl(scope: FrameWindowScope) : OleboWindowScope, FrameWindowScope by scope {
    private val observers = mutableListOf<() -> Unit>()

    override fun addSettingsChangedListener(action: () -> Unit) {
        observers.add(action)
    }

    override fun triggerSettingsChange() {
        observers.forEach { it.invoke() }
    }
}

@Composable
fun ApplicationScope.Window(
    title: String,
    size: DpSize,
    minimumSize: DpSize? = null,
    placement: WindowPlacement = WindowPlacement.Floating,
    content: @Composable OleboWindowScope.() -> Unit
) {
    val windowState = rememberWindowState(
        size = size,
        position = WindowPosition(Alignment.Center),
        placement = placement
    )

    Window(onCloseRequest = ::exitApplication, state = windowState, title = title, focusable = true) {
        LaunchedEffect(minimumSize) {
            minimumSize?.let {
                window.minimumSize = it.toDimension()
            }
        }

        LaunchedEffect(size) {
            window.preferredSize = size.toDimension()
        }

        val scope = remember { OleboWindowScopeImpl(this) }

        DisposableEffect(Unit) {
            WindowStateManager.composeWindowScopes += scope

            onDispose {
                WindowStateManager.composeWindowScopes -= scope
            }
        }

        content(scope)
    }
}

private fun DpSize.toDimension() = Dimension(width.value.toInt(), height.value.toInt())