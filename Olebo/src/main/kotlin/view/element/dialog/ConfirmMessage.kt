package jdr.exia.view.element.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jdr.exia.localization.STR_CANCEL
import jdr.exia.localization.STR_CONFIRM
import jdr.exia.localization.StringLocale
import jdr.exia.view.element.builder.ContentButtonBuilder
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.tools.withHandCursor

@Composable
fun ConfirmMessage(message: String, title: String, onCloseRequest: DefaultFunction, onConfirm: DefaultFunction) {
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checked, onCheckedChange = { checked = it }, modifier = Modifier.padding(end = 10.dp).withHandCursor())
            Text(text = message, modifier = Modifier.fillMaxWidth())
        }
    }
}