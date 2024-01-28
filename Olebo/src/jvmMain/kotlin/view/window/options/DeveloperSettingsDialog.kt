package jdr.exia.view.window.options

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState
import fr.olebo.sharescene.URL
import fr.olebo.sharescene.UrlProtocol
import fr.olebo.sharescene.domain
import fr.olebo.sharescene.security
import jdr.exia.model.dao.option.Preferences
import jdr.exia.model.dao.reset
import jdr.exia.model.dao.restart
import jdr.exia.view.component.form.DropdownMenu
import jdr.exia.view.component.form.LabeledTextField

@Composable
fun DeveloperSettingsDialog(onCloseRequest: () -> Unit) {
    val state = rememberDialogState(size = DpSize(580.dp, Dp.Unspecified))

    var settings by remember { mutableStateOf(DeveloperSettingsData()) }

    GenericOptionDialog(
        onCloseRequest = onCloseRequest,
        state = state,
        saveSettings = {
            settings.save()
            onCloseRequest()
        },
        onCancel = onCloseRequest,
        onResetDefault = {
            settings = DeveloperSettingsData()
        }
    ) {
        ShareSceneSection(data = settings, updateData = { settings = it })
        Spacer(Modifier.height(10.dp))
        SettingsSection("Reset") {
            Button(
                onClick = {
                    reset()
                    restart()
                },
                content = { Text("Reset & restart", color = Color.Red) }
            )
        }
    }
}

@Composable
private fun DialogSettingsScope.ShareSceneSection(
    data: DeveloperSettingsData,
    updateData: (DeveloperSettingsData) -> Unit
) = SettingsSection("ShareScene") {
    LabeledTextField(
        label = "Nom de domaine :",
        value = data.shareSceneDomain,
        onValueChange = { updateData(data.copy(shareSceneDomain = it)) }
    )

    DropdownMenu(
        label = "Protocole",
        items = remember { UrlProtocol.entries },
        selectedItem = data.shareSceneProtocol,
        onItemSelected = { updateData(data.copy(shareSceneProtocol = it)) }
    )
}

@Immutable
private data class DeveloperSettingsData(
    val shareSceneDomain: String = Preferences.oleboUrl.domain,
    val shareSceneProtocol: UrlProtocol = Preferences.oleboUrl.security
)

private fun DeveloperSettingsData.save() {
    Preferences.oleboUrl = URL("${shareSceneProtocol.value}://$shareSceneDomain")
}