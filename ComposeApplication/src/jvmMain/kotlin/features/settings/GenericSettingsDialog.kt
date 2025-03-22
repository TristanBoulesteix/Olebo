package fr.olebo.features.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.DialogWindowScope
import fr.olebo.resources.Res
import fr.olebo.resources.cancel_button
import fr.olebo.resources.restore_default_settings
import fr.olebo.resources.save_button
import fr.olebo.resources.settings_dialog_title
import org.jetbrains.compose.resources.stringResource

@Immutable
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
fun GenericSettingsDialog(
    onCloseRequest: () -> Unit,
    state: DialogState,
    saveSettings: () -> Unit,
    onResetDefault: () -> Unit,
    onCancel: () -> Unit,
    content: @Composable DialogSettingsScope.() -> Unit
) = DialogWindow(
    onCloseRequest = onCloseRequest,
    state = state,
    resizable = false,
    title = stringResource(Res.string.settings_dialog_title),
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
            content = { Text(stringResource(Res.string.save_button)) }
        )
        OutlinedButton(onClick = onReset, modifier = buttonModifier) { Text(stringResource(Res.string.cancel_button)) }
        OutlinedButton(
            onClick = onRestoreDefault,
            modifier = buttonModifier,
            content = { Text(stringResource(Res.string.restore_default_settings)) }
        )
    }