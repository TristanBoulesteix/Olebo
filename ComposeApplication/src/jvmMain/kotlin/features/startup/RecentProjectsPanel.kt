package fr.olebo.features.startup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.olebo.resources.Res
import fr.olebo.resources.search_in_recent_projects
import org.jetbrains.compose.resources.stringResource

@Composable
fun RecentProjectsPanel() = Card(Modifier.fillMaxSize().padding(15.dp)) {
    Column(Modifier.fillMaxSize()) {
        var searchString by remember { mutableStateOf("") }

        TextField(value = searchString, onValueChange = { searchString = it }, singleLine = true, placeholder = { Text(stringResource(Res.string.search_in_recent_projects)) }, modifier = Modifier.fillMaxWidth())
    }
}