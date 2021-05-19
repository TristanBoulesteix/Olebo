@file:Suppress("FunctionName")

package jdr.exia.view.element

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
import jdr.exia.model.tools.Result
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.tools.MessageType
import jdr.exia.view.tools.showMessage
import jdr.exia.view.tools.withFocusCursor

@Composable
fun FooterRow(
    lazyResult: Lazy<Result>,
    isEnabled: Boolean = true,
    onDone: DefaultFunction,
    onCancel: DefaultFunction = onDone
) = Row(
    horizontalArrangement = Arrangement.SpaceAround,
    modifier = Modifier.fillMaxWidth().padding(15.dp)
) {
    OutlinedButton(
        onClick = {
            when (val result = lazyResult.value) {
                is Result.Failure -> showMessage(message = result.message, messageType = MessageType.WARNING)
                is Result.Success -> onDone()
            }
        },
        enabled = isEnabled,
        content = { Text(text = StringLocale[STR_CONFIRM]) },
        modifier = Modifier.withFocusCursor()
    )
    OutlinedButton(
        onClick = if (!isEnabled) onCancel else onDone,
        content = {
            Text(text = StringLocale[STR_CANCEL])
        },
        modifier = Modifier.withFocusCursor()
    )
}
