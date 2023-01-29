package jdr.exia.view.window.options

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindowScope
import jdr.exia.localization.*

sealed interface DialogSettingsScope : DialogWindowScope, ColumnScope {
    @Composable
    fun SettingsSection(sectionTitle: String, content: @Composable ColumnScope.() -> Unit)
}

@Immutable
private class ScopeImpl(windowScope: DialogWindowScope, columnScope: ColumnScope) : DialogSettingsScope,
    DialogWindowScope by windowScope, ColumnScope by columnScope {
    @Composable
    override fun SettingsSection(
        sectionTitle: String,
        content: @Composable ColumnScope.() -> Unit
    ) = Column(
        modifier = Modifier.fillMaxWidth().padding(5.dp)
            .border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(5.dp)).padding(10.dp),
        content = {
            Text(sectionTitle, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
            Spacer(Modifier.height(8.dp))
            content()
        }
    )
}

@Composable
fun GenericOptionDialog(
    onCloseRequest: () -> Unit,
    state: DialogState,
    saveSettings: () -> Unit,
    onResetDefault: () -> Unit,
    onCancel: () -> Unit,
    content: @Composable DialogSettingsScope.() -> Unit
) = Dialog(
    onCloseRequest = onCloseRequest,
    state = state,
    resizable = false,
    title = StringLocale[STR_OPTIONS]
) {
    val dialogScope = this

    Card(modifier = Modifier.fillMaxSize()) {
        Column {
            content(ScopeImpl(dialogScope, this))
            Spacer(Modifier.height(10.dp))
            RowButton(
                onSave = saveSettings,
                onRestoreDefault = onResetDefault,
                onReset = onCancel
            )
        }
    }
}

@Composable
private fun RowButton(onSave: () -> Unit, onReset: () -> Unit, onRestoreDefault: () -> Unit) =
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().padding(5.dp)) {
        val buttonModifier = Modifier.padding(4.dp)

        OutlinedButton(
            onClick = onSave,
            modifier = buttonModifier,
            content = { Text(StringLocale[STR_SAVE]) }
        )
        OutlinedButton(onClick = onReset, modifier = buttonModifier) { Text(StringLocale[STR_CANCEL]) }
        OutlinedButton(
            onClick = onRestoreDefault,
            modifier = buttonModifier,
            content = { Text(StringLocale[STR_RESTORE_DEFAULTS_OPTIONS]) }
        )
    }