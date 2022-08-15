package jdr.exia.view.windows.options

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindowScope
import jdr.exia.localization.STR_OPTIONS
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get

@Composable
fun GenericOptionDialog(
    onCloseRequest: () -> Unit,
    state: DialogState,
    content: @Composable DialogWindowScope.() -> Unit
) = Dialog(
    onCloseRequest = onCloseRequest,
    state = state,
    resizable = false,
    title = StringLocale[STR_OPTIONS]
) {
    Card(modifier = Modifier.fillMaxSize()) {
        content()
    }
}