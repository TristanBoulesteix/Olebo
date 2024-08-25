package fr.olebo.features.startup

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ApplicationScope
import fr.olebo.application.style.smallWindowDimension
import fr.olebo.components.OleboWindow
import fr.olebo.domain.viewmodels.StartupViewModel
import fr.olebo.resources.Res
import fr.olebo.resources.create_new_scenario
import fr.olebo.resources.open_scenario
import fr.olebo.resources.startup_window_title
import org.jetbrains.compose.resources.stringResource
import org.kodein.di.compose.rememberInstance

/**
 * Composable function representing the startup window of the application.
 *
 * Displays a window with a title and content. The title is obtained from the string resource,
 * and the content is a column of composable elements. The window size and minimum size are optional
 * and can be specified. The window is created using the [OleboWindow] function.
 *
 * @see OleboWindow
 * @see Title
 * @see Content
 */
@Composable
fun ApplicationScope.StartupWindow() {
    val title = stringResource(Res.string.startup_window_title)

    OleboWindow(
        title = title,
        size = smallWindowDimension,
        minimumSize = smallWindowDimension
    ) {
        Column {
            Title(title)
            Content(Modifier.padding(top = 8.dp))
        }
    }
}

/**
 * Creates a title component for displaying a text.
 *
 * @param title The text to be displayed as the title.
 */
@Composable
private fun Title(title: String) = Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colors.onBackground)
}

/**
 * Composable function that represents the content of the startup window.
 *
 * @param modifier The modifier for styling the content.
 * @see ButtonBar
 * @see RecentScenariosPanel
 */
@Composable
private fun Content(modifier: Modifier) = Column(modifier) {
    val viewModel: StartupViewModel by rememberInstance()

    ButtonBar(viewModel::createScenario)
    RecentScenariosPanel(viewModel.getRecentScenarios(), viewModel::createScenario)
}

/**
 * A composable function that renders a button bar with two buttons: "Create New Scenario" and "Open Scenario".
 *
 * @param createScenario lambda function to be invoked when the "Create New Scenario" button is clicked.
 */
@Composable
private fun ButtonBar(createScenario: () -> Unit) = Row(
    Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally)
) {
    Button(onClick = createScenario) {
        Text(stringResource(Res.string.create_new_scenario))
    }
    Button(onClick = { TODO() }) {
        Text(stringResource(Res.string.open_scenario))
    }
}