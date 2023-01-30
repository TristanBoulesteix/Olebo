package jdr.exia.view.window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import androidx.compose.ui.zIndex
import jdr.exia.DeveloperModeManager
import jdr.exia.SimpleFunction
import jdr.exia.view.tools.preventClickThrough
import jdr.exia.view.ui.roundedShape
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.Dimension
import java.awt.Window

val LocalWindow = staticCompositionLocalOf<OleboWindowStatus?> { null }

@Immutable
sealed interface OleboWindowStatus {
    val parentWindow: OleboWindowStatus?

    val awtWindow: Window

    fun addSettingsChangedListener(action: SimpleFunction)

    fun triggerSettingsChanged()
}

@Immutable
private class OleboWindowStatusImpl(
    override val parentWindow: OleboWindowStatus?,
    override val awtWindow: Window
) : OleboWindowStatus {
    private val observers = mutableListOf<SimpleFunction>()

    override fun addSettingsChangedListener(action: SimpleFunction) {
        observers.add(action)
    }

    override fun triggerSettingsChanged() {
        observers.forEach { it.invoke() }
    }
}

@Stable
private class PopupManagerImpl : PopupManager {
    override var content by mutableStateOf<PopupContent?>(null)

    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = System.identityHashCode(this)
}

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

        val parentWindow = LocalWindow.current

        val localPopup = remember { PopupManagerImpl() }

        CompositionLocalProvider(
            LocalWindow provides remember { OleboWindowStatusImpl(parentWindow, window) },
            LocalPopup provides localPopup
        ) {
            Popup()
            content(this)
        }
    }
}

@Composable
private fun Popup() {
    val currentPopup = LocalPopup.current!!

    val currentPopupContext: PopupContext = remember {
        object : PopupContext {
            override var backgroundColor by mutableStateOf(Color.Transparent)

            override var shape: Shape by mutableStateOf(roundedShape)

            override var fractionContent by mutableStateOf(.9f)
        }
    }

    currentPopup.content?.let { popupContent ->
        Box(
            modifier = Modifier.fillMaxSize().zIndex(1000f).background(Color.Black.copy(alpha = .8f))
                .preventClickThrough(),
            contentAlignment = Alignment.Center
        ) {
            Popup(
                alignment = Alignment.Center,
                focusable = true,
                onDismissRequest = {
                    runBlocking {
                        currentPopup.content = null
                        // Delay is required to prevent incidental clicks
                        delay(100)
                    }
                }
            ) {
                Card(
                    elevation = 15.dp,
                    backgroundColor = currentPopupContext.backgroundColor,
                    shape = currentPopupContext.shape,
                    modifier = Modifier.fillMaxSize(currentPopupContext.fractionContent)
                ) {
                    popupContent(currentPopupContext)
                }
            }
        }
    }
}

private fun DpSize.toDimension() = Dimension(width.value.toInt(), height.value.toInt())

private class JobHolder(var value: Job? = null)