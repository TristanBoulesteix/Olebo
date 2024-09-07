package fr.olebo.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.DialogWindowScope
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState


/**
 * Displays a dialog window with a specified title and content.
 *
 * The dialog is not resizable, and its position and size can be defined.
 *
 * @param onCloseRequest A callback function to be invoked when the dialog is closed.
 * @param title The title of the dialog window.
 * @param size The dimensions of the dialog window, defaulting to 400x300 dp.
 * @param content The composable content to be displayed inside the dialog.
 */
@Composable
fun OleboDialog(
    onCloseRequest: () -> Unit,
    title: String,
    size: DpSize = DpSize(400.dp, 300.dp),
    content: @Composable DialogWindowScope.() -> Unit
) = DialogWindow(
    onCloseRequest = onCloseRequest,
    resizable = false,
    title = title,
    state = rememberDialogState(
        position = WindowPosition(Alignment.Center),
        size = size,
    )
) {
    Surface(modifier = Modifier.fillMaxSize()) { content() }
}