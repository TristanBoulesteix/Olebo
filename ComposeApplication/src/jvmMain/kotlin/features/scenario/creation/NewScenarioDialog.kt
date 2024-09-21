package fr.olebo.features.scenario.creation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import fr.olebo.components.window.OleboDialog
import fr.olebo.resources.Res
import fr.olebo.resources.cancel_button
import fr.olebo.resources.enter_name_for_scenario_tooltip
import fr.olebo.resources.ok_button
import fr.olebo.resources.scenario_creation_dialog_title
import org.jetbrains.compose.resources.stringResource

/**
 * Displays a dialog window for creating a new scenario.
 *
 * The dialog includes a text field for entering the scenario name and buttons to cancel
 * or submit the creation request. The submission button is enabled only if the scenario
 * name is validated successfully.
 *
 * @param onClose The callback function to be invoked when the dialog is closed.
 * @param validateName The function to validate the entered scenario name.
 * @param createScenario The function to handle the creation of the scenario with the given name.
 */
@Composable
fun WindowScope.NewScenarioDialog(
    onClose: () -> Unit,
    validateName: (String) -> Boolean,
    createScenario: (scenarioName: String) -> Unit
) {
    val title = stringResource(Res.string.scenario_creation_dialog_title)

    var scenarioName by remember { mutableStateOf("") }

    val submitScenario = {
        createScenario(scenarioName)
        onClose()
    }

    OleboDialog(
        onCloseRequest = onClose,
        title = title,
        size = DpSize(400.dp, 180.dp),
        onKeyEvent = {
            if (it.key == Key.Enter && validateName(scenarioName)) {
                submitScenario()
                true
            } else if (it.key == Key.Escape) {
                onClose()
                true
            } else {
                false
            }
        }
    ) {
        Column(
            Modifier.fillMaxSize().padding(4.dp).padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val focusRequester = remember(::FocusRequester)

            TextField(
                value = scenarioName,
                onValueChange = { scenarioName = it },
                modifier = Modifier.padding(top = 8.dp).width(300.dp).focusRequester(focusRequester),
                placeholder = { Text(stringResource(Res.string.enter_name_for_scenario_tooltip)) },
                singleLine = true
            )

            ButtonRow(
                isNameValid = validateName(scenarioName),
                onCancel = onClose,
                submitScenario = submitScenario
            )

            LaunchedEffect(Unit) { focusRequester.requestFocus() }
        }
    }
}

/**
 * Represents a row of buttons for canceling or submitting a scenario.
 *
 * @param isNameValid A boolean indicating whether the current scenario name is valid.
 * @param onCancel A callback function to be invoked when the cancel button is clicked.
 * @param submitScenario A callback function to be invoked when the submit button is clicked.
 */
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