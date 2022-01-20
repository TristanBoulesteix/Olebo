package jdr.exia.view.composable.master

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.olebo.sharescene.Connected
import fr.olebo.sharescene.ConnectionState
import fr.olebo.sharescene.Disconnected
import fr.olebo.sharescene.ShareSceneManager
import jdr.exia.localization.*
import jdr.exia.model.tools.saveToClipboard
import jdr.exia.view.element.FlowRow

@Composable
fun ShareScenePanel(
    connect: () -> Unit,
    connectionState: ConnectionState
) = Box(Modifier.padding(5.dp).fillMaxSize(), contentAlignment = Alignment.CenterStart) {
    when (connectionState) {
        is Connected -> ShareSceneManagerScreen(
            connectionState.manager,
            connectionState.shareSceneViewModel.numberOfConnectedUser
        )
        else -> WebConfig(connect, connectionState)
    }
}

@Composable
private fun WebConfig(
    connect: () -> Unit,
    connectionState: ConnectionState
) = Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
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

    if (connectionState is Disconnected.ConnectionFailed)
        Text(
            StringLocale[STR_ERROR_LOGIN_TO_OLEBO_WEB],
            Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp),
            color = Color.Red
        )
}

@Composable
private fun ShareSceneManagerScreen(
    manager: ShareSceneManager,
    numberOfConnectedUser: Int
) = Column(modifier = Modifier.padding(start = 10.dp)) {
    Text("Number of connected user: $numberOfConnectedUser")

    Spacer(Modifier.height(25.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(value = manager.sceneUrl.orEmpty(), onValueChange = {})

        Spacer(Modifier.width(25.dp))

        FlowRow(Modifier.fillMaxWidth()) {
            Button(onClick = { saveToClipboard(manager.codeSession.orEmpty()) }) {
                Text("Copy the code to the clipboard")
            }

            Spacer(Modifier.width(25.dp))

            Button(onClick = { saveToClipboard(manager.sceneUrl.orEmpty()) }) {
                Text("Copy url to the clipboard")
            }
        }
    }
}