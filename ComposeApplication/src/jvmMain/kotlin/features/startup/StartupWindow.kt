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
import fr.olebo.components.Window
import fr.olebo.resources.Res
import fr.olebo.resources.create_new_project
import fr.olebo.resources.open_project
import fr.olebo.resources.startup_window_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ApplicationScope.StartupWindow() {
    val title = stringResource(Res.string.startup_window_title)

    Window(
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

@Composable
private fun Title(title: String) = Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colors.onBackground)
}

@Composable
private fun Content(modifier: Modifier) = Column(modifier) {
    ButtonBar()
    RecentProjectsPanel()
}

@Composable
private fun ButtonBar() = Row(
    Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally)
) {
    Button(onClick = { TODO() }) {
        Text(stringResource(Res.string.create_new_project))
    }
    Button(onClick = { TODO() }) {
        Text(stringResource(Res.string.open_project))
    }
}