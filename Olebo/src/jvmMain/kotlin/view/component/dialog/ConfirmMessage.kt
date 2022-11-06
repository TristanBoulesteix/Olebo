package jdr.exia.view.component.dialog

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import jdr.exia.localization.STR_CANCEL
import jdr.exia.localization.STR_CONFIRM
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get
import jdr.exia.view.component.builder.ContentButtonBuilder
import jdr.exia.view.component.form.LabeledCheckbox

@Composable
fun ConfirmMessage(
    message: String,
    title: String,
    doubleCheck: Boolean = true,
    onCloseRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    var checked by remember { mutableStateOf(!doubleCheck) }

    MessageDialog(
        title = title,
        buttonsBuilder = listOf(
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
        height = 190.dp
    ) {
        if (doubleCheck) LabeledCheckbox(checked = checked, onCheckedChange = { checked = it }, label = message)
        else Text(message)
    }
}