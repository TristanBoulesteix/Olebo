package fr.olebo.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.DialogWindowScope
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState

@Composable
fun OleboDialog(
    onCloseRequest: () -> Unit,
    title: String,
    content: @Composable DialogWindowScope.() -> Unit
) = DialogWindow(
    onCloseRequest = onCloseRequest,
    resizable = false,
    title = title,
    state = rememberDialogState(
        position = WindowPosition(Alignment.Center)
    )
) {
    Surface(modifier = Modifier.fillMaxSize()) { content() }
}