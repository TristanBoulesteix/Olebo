package fr.olebo.features.create_scenario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import fr.olebo.components.OleboDialog
import fr.olebo.resources.Res
import fr.olebo.resources.cancel_button
import fr.olebo.resources.enter_name_for_scenario_tooltip
import fr.olebo.resources.ok_button
import fr.olebo.resources.scenario_creation_dialog_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun WindowScope.NewScenarioDialog(onClose: () -> Unit, createScenario: (scenarioName: String) -> Unit) {
    val title = stringResource(Res.string.scenario_creation_dialog_title)

    OleboDialog(
        onCloseRequest = onClose,
        title = title,
        size = DpSize(400.dp, 180.dp),
    ) {
        Column(Modifier.fillMaxSize().padding(4.dp).padding(top = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            var scenarioName by remember { mutableStateOf("") }

            TextField(
                value = scenarioName,
                onValueChange = { scenarioName = it },
                modifier = Modifier.padding(top = 8.dp),
                placeholder = { Text(stringResource(Res.string.enter_name_for_scenario_tooltip)) }
            )

            ButtonRow(
                isNameValid = scenarioName.isNotBlank(),
                onCancel = onClose,
                submitScenario = {
                    createScenario(scenarioName)
                    onClose()
                }
            )
        }
    }
}

@Composable
private fun ButtonRow(isNameValid: Boolean, onCancel: () -> Unit, submitScenario: () -> Unit) = Row(
    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
    horizontalArrangement = Arrangement.SpaceAround
) {
    Button(onClick = onCancel) {
        Text(stringResource(Res.string.cancel_button))
    }

    Button(onClick = submitScenario, enabled = isNameValid) {
        Text(stringResource(Res.string.ok_button))
    }
}