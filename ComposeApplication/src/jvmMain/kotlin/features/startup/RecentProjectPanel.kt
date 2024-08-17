package fr.olebo.features.startup

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fr.olebo.resources.Res
import fr.olebo.resources.recent_project
import org.jetbrains.compose.resources.stringResource

@Composable
fun RecentProjectPanel(modifier: Modifier) = Row(modifier.fillMaxSize()) {
    Text(stringResource(Res.string.recent_project), fontWeight = FontWeight.Bold, fontSize = 18.sp)
}

@Composable
private fun ProjectItem() = Row {


}