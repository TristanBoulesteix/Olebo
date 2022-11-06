package jdr.exia.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jdr.exia.localization.STR_CANCEL
import jdr.exia.localization.STR_CONFIRM
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get
import jdr.exia.model.tools.SimpleResult
import jdr.exia.view.tools.MessageType
import jdr.exia.view.tools.showMessage

@Composable
fun FooterRowWithCancel(
    modifier: Modifier = Modifier,
    confirmText: String = StringLocale[STR_CONFIRM],
    onConfirm: () -> SimpleResult,
    cancelText: String = StringLocale[STR_CANCEL],
    onDone: () -> Unit,
    onFailure: (Throwable) -> Unit = {
        if (it.message != null)
            showMessage(message = it.message!!, messageType = MessageType.WARNING)
    }
) = Row(
    horizontalArrangement = Arrangement.SpaceAround,
    modifier = Modifier.fillMaxWidth().padding(15.dp) then modifier
) {
    OutlinedButton(
        onClick = {
            onConfirm().onSuccess { onDone() }.onFailure(onFailure)
        },
        content = { Text(text = confirmText) }
    )

    OutlinedButton(
        onClick = onDone,
        content = { Text(text = cancelText) }
    )
}