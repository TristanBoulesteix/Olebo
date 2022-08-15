package jdr.exia.view.windows.options

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState

@Composable
fun DeveloperSettingsDialog(onCloseRequest: () -> Unit) {
    val state = rememberDialogState(size = DpSize(580.dp, 635.dp))

    GenericOptionDialog(onCloseRequest = onCloseRequest, state = state) {
        Text("TODO")
    }
}