package jdr.exia.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import jdr.exia.localization.*
import jdr.exia.model.dao.SettingsTable
import jdr.exia.model.dao.option.SerializableColor
import jdr.exia.model.dao.option.SerializableLabelState
import jdr.exia.model.dao.option.Settings
import jdr.exia.view.element.form.DropDownMenu
import jdr.exia.view.element.form.LabeledCheckbox
import jdr.exia.view.tools.withHandCursor
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun SettingsDialog(onCloseRequest: () -> Unit) {
    val state = rememberDialogState(size = WindowSize(550.dp, 450.dp))

    val originalSettings = remember { dataFromSettings }

    var settings by remember {
        mutableStateOf(originalSettings)
    }

    Dialog(onCloseRequest = onCloseRequest, state = state, resizable = false, title = StringLocale[STR_OPTIONS]) {
        Column {
            GeneralSettings(settingsData = settings, updateSettings = { settings = it })
            Spacer(Modifier.height(10.dp))
            LookAndFeelSettings(settingsData = settings, updateSettings = { settings = it })
            Spacer(Modifier.height(10.dp))
            RowButton(
                close = onCloseRequest,
                data = settings,
                refresh = { settings = dataFromSettings },
                closeAndReset = {
                    originalSettings.save()
                    onCloseRequest()
                }
            )
        }
    }
}

@Composable
private fun GeneralSettings(settingsData: SettingsData, updateSettings: (SettingsData) -> Unit) =
    SettingsSection(StringLocale[STR_GENERAL]) {
        val locales = remember { availableLocales }

        DropDownMenu(
            items = locales,
            selectedItem = settingsData.language,
            onItemSelected = { updateSettings(settingsData.copy(language = it)) },
            label = StringLocale[STR_SOFTWARE_LANGUAGE_LABEL]
        )

        LabeledCheckbox(
            checked = settingsData.autoUpdateEnabled,
            onCheckedChange = { updateSettings(settingsData.copy(autoUpdateEnabled = it)) },
            label = StringLocale[STR_AUTO_UPDATE]
        )
    }

@Composable
private fun LookAndFeelSettings(settingsData: SettingsData, updateSettings: (SettingsData) -> Unit) =
    SettingsSection(sectionTitle = StringLocale[STR_LOOK_AND_FEEL]) {
        LabeledCheckbox(
            checked = settingsData.autoOpenPlayerDialog,
            onCheckedChange = { updateSettings(settingsData.copy(autoOpenPlayerDialog = it)) },
            label = StringLocale[STR_PLAYER_FRAME_OPENED]
        )

        val baseColor = remember {
            listOf(
                SerializableColor.PURPLE,
                SerializableColor.YELLOW,
                SerializableColor.RED,
            )
        }

        val cursorColors = remember {
            listOf(
                SerializableColor.BLACK_WHITE,
                SerializableColor.WHITE_BLACK,
            ) + baseColor
        }

        DropDownMenu(
            items = cursorColors,
            selectedItem = settingsData.cursorColor,
            onItemSelected = { updateSettings(settingsData.copy(cursorColor = it)) },
            label = StringLocale[STR_CURSOR_COLOR_LABEL]
        )

        LabeledCheckbox(
            checked = settingsData.elementsAreVisibleByDefault,
            onCheckedChange = { updateSettings(settingsData.copy(elementsAreVisibleByDefault = it)) },
            label = StringLocale[STR_DEFAULT_ELEMENT_VISIBILITY]
        )

        val labelStates = remember { SerializableLabelState.values().toList() }

        DropDownMenu(
            items = labelStates,
            selectedItem = settingsData.labelState,
            onItemSelected = { updateSettings(settingsData.copy(labelState = it)) },
            label = StringLocale[STR_LABEL_STATE]
        )

        Spacer(Modifier.height(5.dp))

        val labelColor = remember {
            listOf(SerializableColor.BLACK) + baseColor
        }

        DropDownMenu(
            items = labelColor,
            selectedItem = settingsData.labelColor,
            onItemSelected = { updateSettings(settingsData.copy(labelColor = it)) },
            label = StringLocale[STR_LABEL_COLOR]
        )
    }

@Composable
private inline fun SettingsSection(sectionTitle: String, content: @Composable ColumnScope.() -> Unit) = Column(
    modifier = Modifier.fillMaxWidth().padding(5.dp).border(2.dp, Color.Black, RoundedCornerShape(5.dp)).padding(10.dp),
    content = {
        Text(sectionTitle, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
        Spacer(Modifier.height(8.dp))
        content()
    }
)

@Composable
private fun RowButton(data: SettingsData, refresh: () -> Unit, close: () -> Unit, closeAndReset: () -> Unit) =
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(5.dp)) {
        val buttonModifier = Modifier.padding(4.dp).withHandCursor()

        OutlinedButton(
            onClick = {
                data.save()
                close()
            },
            modifier = buttonModifier,
            content = { Text(StringLocale[STR_SAVE]) }
        )
        OutlinedButton(onClick = closeAndReset, modifier = buttonModifier) { Text(StringLocale[STR_CANCEL]) }
        OutlinedButton(
            onClick = {
                transaction { SettingsTable.initializeDefault() }
                refresh()
            },
            modifier = buttonModifier,
            content = { Text(StringLocale[STR_RESTORE_DEFAULTS_OPTIONS]) }
        )
    }

@Immutable
private data class SettingsData(
    val language: Language,
    val autoUpdateEnabled: Boolean,
    val autoOpenPlayerDialog: Boolean,
    val cursorColor: SerializableColor,
    val elementsAreVisibleByDefault: Boolean,
    val labelState: SerializableLabelState,
    val labelColor: SerializableColor
)

private fun SettingsData.save() {
    Settings.language = language
    Settings.autoUpdate = autoUpdateEnabled
    Settings.playerFrameOpenedByDefault = autoOpenPlayerDialog
    Settings.cursorColor = cursorColor
    Settings.defaultElementVisibility = elementsAreVisibleByDefault
    Settings.labelState = labelState
    Settings.labelColor = labelColor
}

private val dataFromSettings
    get() = SettingsData(
        Settings.language,
        Settings.autoUpdate,
        Settings.playerFrameOpenedByDefault,
        Settings.cursorColor,
        Settings.defaultElementVisibility,
        Settings.labelState,
        Settings.labelColor
    )