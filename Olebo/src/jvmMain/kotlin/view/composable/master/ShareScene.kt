package jdr.exia.view.composable.master

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.olebo.sharescene.Connected
import fr.olebo.sharescene.ConnectionState
import fr.olebo.sharescene.Disconnected
import fr.olebo.sharescene.ShareSceneManager
import jdr.exia.localization.STR_LOGIN_OLEBO_WEB
import jdr.exia.localization.STR_START_OLEBO_WEB
import jdr.exia.localization.ST_OLEBO_WEB_EXPLANATION
import jdr.exia.localization.StringLocale
import jdr.exia.viewModel.ShareSceneViewModel

@Composable
fun ShareScenePanel(
    connect: () -> Unit,
    shareSceneViewModel: ShareSceneViewModel
) = Box(Modifier.padding(5.dp).fillMaxSize(), contentAlignment = Alignment.CenterStart) {
    when (val connectionState = shareSceneViewModel.connectionState) {
        is Connected -> ShareSceneManagerScreen(connectionState.manager, shareSceneViewModel.numberOfConnectedUser)
        else -> WebConfig(connect, connectionState)
    }
}

@Composable
private fun WebConfig(connect: () -> Unit, connectionState: ConnectionState) = Row {
    Text(StringLocale[ST_OLEBO_WEB_EXPLANATION])

    Spacer(Modifier.width(8.dp))

    Button(
        onClick = connect,
        modifier = Modifier.padding(horizontal = 8.dp),
        enabled = connectionState is Disconnected
    ) {
        Text(StringLocale[if (connectionState is Disconnected) STR_START_OLEBO_WEB else STR_LOGIN_OLEBO_WEB])
    }
}

@Composable
private fun ShareSceneManagerScreen(
    manager: ShareSceneManager,
    numberOfConnectedUser: Int
) = Column(modifier = Modifier.padding(start = 10.dp)) {
    Text("Number of connected user: $numberOfConnectedUser")

    Spacer(Modifier.height(25.dp))

    OutlinedTextField(value = manager.sceneUrl ?: "", onValueChange = {})
}