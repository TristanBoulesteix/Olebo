package jdr.exia.view.composable.master

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.olebo.sharescene.*
import jdr.exia.localization.*
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.tools.saveToClipboard
import jdr.exia.view.element.FlowRow
import jdr.exia.view.tools.BoxWithTooltipIfNotNull
import java.util.Locale

@Composable
fun ShareScenePanel(
    connect: () -> Unit,
    disconnect: () -> Unit,
    connectionState: ConnectionState
) = Box(Modifier.padding(5.dp).fillMaxSize(), contentAlignment = Alignment.CenterStart) {
    when (connectionState) {
        is Connected -> ShareSceneManagerScreen(
            manager = connectionState.manager,
            shareSceneViewModel = connectionState.shareSceneViewModel,
            disconnect = disconnect
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
    shareSceneViewModel: ShareSceneViewModel,
    disconnect: () -> Unit
) = Row(modifier = Modifier.padding(start = 10.dp)) {
    Column(verticalArrangement = Arrangement.Center) {
        Spacer(Modifier.height(12.dp))

        Box(contentAlignment = Alignment.Center) {
            val playersNameList = remember(shareSceneViewModel.connectedPlayers) {
                if (shareSceneViewModel.connectedPlayers.isNotEmpty()) buildString {
                    appendLine(StringLocale[ST_INT1_NAME_OF_CONNECTED_PLAYERS, shareSceneViewModel.numberOfConnectedPlayers])

                    shareSceneViewModel.connectedPlayers.forEach {
                        appendLine("- ${it.name}")
                    }
                } else null
            }

            BoxWithTooltipIfNotNull(playersNameList, tooltipAlignment = Alignment.CenterEnd) {
                Text(StringLocale[ST_INT1_NUMBER_OF_CONNECTED_PLAYERS, shareSceneViewModel.numberOfConnectedPlayers])
            }
        }

        Spacer(Modifier.height(13.dp))

        OutlinedTextField(value = manager.sceneUrl.orEmpty(), onValueChange = {})
    }

    Spacer(Modifier.width(25.dp))

    Column {
        Button(
            onClick = disconnect,
            colors = buttonColors(backgroundColor = Color.Red, contentColor = Color.White)
        ) {
            Text(StringLocale[STR_LOGOUT])
        }

        FlowRow(Modifier.fillMaxWidth()) {
            val buttonSizeModifier = remember {
                val width = if (Settings.language == Language(Locale.FRENCH)) 380.dp else 300.dp
                Modifier.width(width)
            }

            Button(modifier = buttonSizeModifier, onClick = { saveToClipboard(manager.codeSession.orEmpty()) }) {
                Text(StringLocale[STR_COPY_CODE])
            }

            Spacer(Modifier.width(25.dp))

            Button(modifier = buttonSizeModifier, onClick = { saveToClipboard(manager.sceneUrl.orEmpty()) }) {
                Text(StringLocale[STR_COPY_URL])
            }
        }
    }
}