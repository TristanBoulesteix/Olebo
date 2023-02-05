package jdr.exia.view.window.options

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.rememberDialogState
import jdr.exia.localization.*
import jdr.exia.model.dao.SettingsTable
import jdr.exia.model.dao.option.SerializableColor
import jdr.exia.model.dao.option.SerializableLabelState
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.type.JColor
import jdr.exia.model.type.toColor
import jdr.exia.model.type.toJColor
import jdr.exia.view.component.form.DropdownMenu
import jdr.exia.view.component.form.LabeledCheckbox
import org.jetbrains.exposed.sql.transactions.transaction
import javax.swing.JColorChooser

@Composable
fun SettingsDialog(onCloseRequest: () -> Unit) {
    val state = rememberDialogState(size = DpSize(580.dp, Dp.Unspecified))

    val originalSettings = remember { dataFromSettings }

    var settings by remember {
        mutableStateOf(originalSettings)
    }

    GenericOptionDialog(
        onCloseRequest = onCloseRequest,
        state = state,
        onResetDefault = {
            transaction { SettingsTable.initializeDefault() }
            settings = dataFromSettings
        },
        saveSettings = {
            settings.save()
            onCloseRequest()
        },
        onCancel = {
            originalSettings.save()
            onCloseRequest()
        }
    ) {
        GeneralSettings(settingsData = settings, updateSettings = { settings = it })
        Spacer(Modifier.height(10.dp))
        LookAndFeelSettings(settingsData = settings, updateSettings = { settings = it })
    }
}

@Composable
private fun DialogSettingsScope.GeneralSettings(settingsData: SettingsData, updateSettings: (SettingsData) -> Unit) =
    SettingsSection(StringLocale[STR_GENERAL]) {
        val locales = remember { availableLocales }

        DropdownMenu(
            items = locales,
            selectedItem = settingsData.language,
            onItemSelected = { updateSettings(settingsData.copy(language = it)) },
            label = StringLocale[STR_SOFTWARE_LANGUAGE_LABEL]
        )

        if (settingsData.language != Settings.language) {
            Text(text = StringLocale[ST_LANGUAGE_CHANGE_ON_RESTART], color = Color.Red, fontSize = 12.sp)
        }

        LabeledCheckbox(
            checked = settingsData.autoUpdateEnabled,
            onCheckedChange = { updateSettings(settingsData.copy(autoUpdateEnabled = it)) },
            label = StringLocale[STR_AUTO_UPDATE]
        )
    }

@Composable
private fun DialogSettingsScope.LookAndFeelSettings(
    settingsData: SettingsData,
    updateSettings: (SettingsData) -> Unit
) =
    SettingsSection(sectionTitle = StringLocale[STR_LOOK_AND_FEEL]) {
        LabeledCheckbox(
            checked = settingsData.autoOpenPlayerDialog,
            onCheckedChange = { updateSettings(settingsData.copy(autoOpenPlayerDialog = it)) },
            label = StringLocale[STR_PLAYER_FRAME_OPENED]
        )

        LabeledCheckbox(
            checked = settingsData.playerWindowFullScreen,
            onCheckedChange = { updateSettings(settingsData.copy(playerWindowFullScreen = it)) },
            label = StringLocale[ST_SHOULD_OPEN_PLAYER_FRAME_FULL_SCREEN]
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
            ) + baseColor + SerializableColor.Custom.default
        }

        DropdownMenu(
            items = cursorColors,
            selectedItem = settingsData.cursorColor,
            selectedContent = selectedContentColor,
            onItemSelected = { newColor ->
                updateColor(
                    newColor = newColor,
                    defaultColor = settingsData.cursorColor,
                    updateSettings = { updateSettings(settingsData.copy(cursorColor = it)) })
            },
            label = StringLocale[STR_CURSOR_COLOR_LABEL]
        )

        LabeledCheckbox(
            checked = settingsData.elementsAreVisibleByDefault,
            onCheckedChange = { updateSettings(settingsData.copy(elementsAreVisibleByDefault = it)) },
            label = StringLocale[STR_DEFAULT_ELEMENT_VISIBILITY]
        )

        val labelStates = remember { SerializableLabelState.values().toList() }

        DropdownMenu(
            items = labelStates,
            selectedItem = settingsData.labelState,
            onItemSelected = { updateSettings(settingsData.copy(labelState = it)) },
            label = StringLocale[STR_LABEL_STATE]
        )

        Spacer(Modifier.height(5.dp))

        val labelColor = remember {
            listOf(SerializableColor.BLACK) + baseColor + SerializableColor.Custom.default
        }

        DropdownMenu(
            items = labelColor,
            selectedItem = settingsData.labelColor,
            selectedContent = selectedContentColor,
            onItemSelected = { newColor ->
                updateColor(
                    newColor = newColor,
                    defaultColor = settingsData.labelColor,
                    updateSettings = { updateSettings(settingsData.copy(labelColor = it)) })
            },
            label = StringLocale[STR_LABEL_COLOR]
        )
    }

@Stable
private val selectedContentColor: @Composable RowScope.(SerializableColor) -> Unit = {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Text(it.toString())
        if (it is SerializableColor.Custom) {
            Spacer(Modifier.size(15.dp))
            Spacer(Modifier.size(15.dp).background(it.color))
        }
    }
}

private fun updateColor(
    newColor: SerializableColor,
    defaultColor: SerializableColor,
    updateSettings: (SerializableColor) -> Unit
) {
    var selectedColor = newColor

    if (newColor is SerializableColor.Custom) {
        val customColor: JColor? =
            JColorChooser.showDialog(null, StringLocale[STR_SELECT_COLOR], newColor.contentColor.toJColor())

        selectedColor = if (customColor == null) defaultColor else SerializableColor.Custom(customColor.toColor())
    }

    updateSettings(selectedColor)
}

@Immutable
private data class SettingsData(
    val language: Language,
    val autoUpdateEnabled: Boolean,
    val autoOpenPlayerDialog: Boolean,
    val cursorColor: SerializableColor,
    val elementsAreVisibleByDefault: Boolean,
    val labelState: SerializableLabelState,
    val labelColor: SerializableColor,
    val playerWindowFullScreen: Boolean
)

private fun SettingsData.save() {
    Settings.language = language
    Settings.autoUpdate = autoUpdateEnabled
    Settings.playerFrameOpenedByDefault = autoOpenPlayerDialog
    Settings.cursorColor = cursorColor
    Settings.defaultElementVisibility = elementsAreVisibleByDefault
    Settings.labelState = labelState
    Settings.labelColor = labelColor
    Settings.playerWindowShouldBeFullScreen = playerWindowFullScreen
}

private val dataFromSettings
    get() = SettingsData(
        Settings.language,
        Settings.autoUpdate,
        Settings.playerFrameOpenedByDefault,
        Settings.cursorColor,
        Settings.defaultElementVisibility,
        Settings.labelState,
        Settings.labelColor,
        Settings.playerWindowShouldBeFullScreen
    )