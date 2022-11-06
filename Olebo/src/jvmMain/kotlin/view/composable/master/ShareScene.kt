package jdr.exia.view.composable.master

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.olebo.sharescene.ShareSceneManager
import fr.olebo.sharescene.ShareSceneViewModel
import fr.olebo.sharescene.connection.Connected
import fr.olebo.sharescene.connection.ConnectionError
import fr.olebo.sharescene.connection.ConnectionState
import fr.olebo.sharescene.connection.Disconnected
import jdr.exia.localization.*
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.tools.saveToClipboard
import jdr.exia.view.component.FlowRow
import jdr.exia.view.tools.BoxWithTooltipIfNotNull
import java.util.Locale

@Composable
fun ShareScenePanel(
    connect: () -> Unit,
    disconnect: () -> Unit,
    connectionState: ConnectionState
) = Surface(Modifier.padding(5.dp).fillMaxSize()) {
    Box(contentAlignment = Alignment.CenterStart) {
        when (connectionState) {
            is Connected -> ShareSceneManagerScreen(
                manager = connectionState.manager,
                shareSceneViewModel = connectionState.shareSceneViewModel,
                disconnect = disconnect
            )
            else -> WebConfig(connect, connectionState)
        }
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
        Box(Modifier.weight(.7f), contentAlignment = Alignment.Center) {
            Text(StringLocale[ST_OLEBO_WEB_EXPLANATION])
        }

        Box(Modifier.weight(.3f), contentAlignment = Alignment.Center) {
            OutlinedButton(
                onClick = connect,
                modifier = Modifier.padding(horizontal = 8.dp),
                enabled = connectionState is Disconnected
            ) {
                Text(StringLocale[if (connectionState is Disconnected) STR_START_OLEBO_WEB else STR_LOGIN_OLEBO_WEB])
            }
        }
    }

    if (connectionState is Disconnected.ConnectionFailed && connectionState.error != ConnectionError.Canceled) {
        Text(
            text = if (connectionState.error is ConnectionError.WrongVersion) StringLocale[ST_ERROR_OLEBO_WEB_VERSION_REQUIREMENT]
            else (connectionState.error as? ConnectionError.ServerError)?.message?.let { StringLocale[ST_STR1_OLEBO_WEB_SERVER_ERROR, it] }
                ?: StringLocale[STR_ERROR_LOGIN_TO_OLEBO_WEB],
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp),
            color = Color.Red
        )
    }
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

        OutlinedTextField(value = manager.sceneUrl.orEmpty(), onValueChange = {}, readOnly = true)
    }

    Spacer(Modifier.width(25.dp))

    Column {
        val buttonSizeModifier = remember {
            // TODO: Find a better way to adapt the size according to text size
            val width = if (Settings.language == Language(Locale.FRENCH)) 380.dp else 300.dp
            Modifier.width(width)
        }

        var isSmallScreen by remember { mutableStateOf(false) }

        OutlinedButton(
            onClick = disconnect,
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Red, contentColor = Color.White),
            modifier = if (isSmallScreen) buttonSizeModifier else Modifier
        ) {
            Text(StringLocale[STR_LOGOUT])
        }

        FlowRow(Modifier.fillMaxWidth(), onRowEvaluated = { isSmallScreen = it > 1 }) {
            OutlinedButton(
                modifier = buttonSizeModifier,
                onClick = { saveToClipboard(manager.codeSession.orEmpty()) }) {
                Text(StringLocale[STR_COPY_CODE])
            }

            Spacer(Modifier.width(25.dp))

            OutlinedButton(modifier = buttonSizeModifier, onClick = { saveToClipboard(manager.sceneUrl.orEmpty()) }) {
                Text(StringLocale[STR_COPY_URL])
            }
        }
    }
}