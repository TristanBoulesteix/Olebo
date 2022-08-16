package jdr.exia.view.windows.options

import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState
import jdr.exia.view.element.form.LabeledTextField

@Composable
fun DeveloperSettingsDialog(onCloseRequest: () -> Unit) {
    val state = rememberDialogState(size = DpSize(580.dp, 635.dp))

    GenericOptionDialog(
        onCloseRequest = onCloseRequest,
        state = state,
        saveSettings = {

        },
        onCancel = {

        },
        onResetDefault = {

        }
    ) {
        ShareSceneSection()
    }
}

@Composable
private fun DialogSettingsScope.ShareSceneSection() = SettingsSection("ShareScene") {
    var url by remember { mutableStateOf("") }

    LabeledTextField("URL :", url, { url = it })
}