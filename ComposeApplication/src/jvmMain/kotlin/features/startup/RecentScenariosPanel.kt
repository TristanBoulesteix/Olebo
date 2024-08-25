package fr.olebo.features.startup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.olebo.domain.models.scenario.ScenarioInfo
import fr.olebo.resources.Res
import fr.olebo.resources.create_new_scenario
import fr.olebo.resources.no_recent_scenario
import fr.olebo.resources.search_in_recent_scenarios
import org.jetbrains.compose.resources.stringResource

@Composable
fun RecentScenariosPanel(recentScenarios: List<ScenarioInfo>, createNewScenario: () -> Unit) = Card(Modifier.fillMaxSize().padding(15.dp)) {
    if (recentScenarios.isNotEmpty()) {
        RecentScenariosList(recentScenarios)
    } else {
        NoRecentScenario(createNewScenario)
    }
}

@Composable
private fun RecentScenariosList(recentScenarios: List<ScenarioInfo>) = Column(Modifier.fillMaxSize()) {
    var searchString by remember { mutableStateOf("") }

    TextField(
        value = searchString,
        onValueChange = { searchString = it },
        singleLine = true,
        placeholder = { Text(stringResource(Res.string.search_in_recent_scenarios)) },
        modifier = Modifier.fillMaxWidth()
    )

    Column(Modifier.fillMaxSize()) {
        recentScenarios.forEach {
            Text(it.name)
        }
    }
}

@Composable
private fun NoRecentScenario(createNewScenario: () -> Unit) = Box(Modifier.fillMaxSize()) {
    Column(Modifier.align(Alignment.Center)) {
        Text(
            text = stringResource(Res.string.no_recent_scenario),
            fontWeight = FontWeight.Bold
        )
        Button(
            onClick = createNewScenario,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(Res.string.create_new_scenario))
        }
    }
}