package fr.olebo.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.DialogWindowScope
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState


/**
 * Displays a dialog window with a specified title and content.
 *
 * @param onCloseRequest A callback function to be invoked when the dialog is requested to be closed.
 * @param title The title of the dialog window.
 * @param size The size of the dialog window. Defaults to DpSize(400.dp, 300.dp).
 * @param onKeyEvent A callback function to handle key events. Defaults to a function that returns false.
 * @param content The content to be displayed within the dialog window.
 */
@Composable
fun OleboDialog(
    onCloseRequest: () -> Unit,
    title: String,
    size: DpSize = DpSize(400.dp, 300.dp),
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable DialogWindowScope.() -> Unit
) = DialogWindow(
    onCloseRequest = onCloseRequest,
    resizable = false,
    title = title,
    state = rememberDialogState(
        position = WindowPosition(Alignment.Center),
        size = size,
    ),
    onPreviewKeyEvent = onKeyEvent
) {
    Surface(modifier = Modifier.fillMaxSize()) { content() }
}