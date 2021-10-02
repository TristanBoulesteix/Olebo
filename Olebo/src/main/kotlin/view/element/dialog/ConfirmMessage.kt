package jdr.exia.view.element.dialog

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import jdr.exia.localization.STR_CANCEL
import jdr.exia.localization.STR_CONFIRM
import jdr.exia.localization.StringLocale
import jdr.exia.view.element.builder.ContentButtonBuilder
import jdr.exia.view.element.form.LabeledCheckbox

@Composable
fun ConfirmMessage(message: String, title: String, onCloseRequest: () -> Unit, onConfirm: () -> Unit) {
    var checked by remember { mutableStateOf(false) }

    PromptDialog(
        title = title,
        buttonBuilders = listOf(
            ContentButtonBuilder(
                content = StringLocale[STR_CONFIRM],
                onClick = {
                    onCloseRequest()
                    onConfirm()
                },
                enabled = checked
            ),
            ContentButtonBuilder(content = StringLocale[STR_CANCEL], onClick = onCloseRequest)
        ),
        onCloseRequest = onCloseRequest,
        width = 500.dp,
        height = 160.dp
    ) {
        LabeledCheckbox(checked = checked, onCheckedChange = { checked = it }, label = message)
    }
}