package jdr.exia.view.windows

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import jdr.exia.DeveloperModeManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    var counterCtrl by remember { mutableStateOf(0) }

    if (counterCtrl > 4) {
        LaunchedEffect(Unit) {
            DeveloperModeManager.toggle()
            counterCtrl = 0
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val jobHolder = remember { JobHolder() }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = title,
        focusable = true,
        onPreviewKeyEvent = {
            if (it.type == KeyEventType.KeyDown) {
                if (it.isCtrlPressed) {
                    counterCtrl++

                    jobHolder.value?.cancel()

                    jobHolder.value = coroutineScope.launch {
                        delay(5_000)
                        counterCtrl = 0
                    }
                } else {
                    counterCtrl = 0
                }
            }

            false
        }
    ) {
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

private class JobHolder(var value: Job? = null)