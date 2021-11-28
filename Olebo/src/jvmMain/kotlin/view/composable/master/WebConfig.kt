package jdr.exia.view.composable.master

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jdr.exia.localization.STR_LOGIN_OLEBO_WEB
import jdr.exia.localization.STR_START_OLEBO_WEB
import jdr.exia.localization.ST_OLEBO_WEB_EXPLANATION
import jdr.exia.localization.StringLocale
import jdr.exia.service.ConnectionState
import jdr.exia.service.Disconnected

@Composable
fun WebConfig(
    connect: () -> Unit,
    connectionState: ConnectionState
) = Box(Modifier.padding(5.dp).fillMaxSize(), contentAlignment = Alignment.CenterStart) {
    Row {
        Text(StringLocale[ST_OLEBO_WEB_EXPLANATION])

        Spacer(Modifier.width(8.dp))

        Button(
            onClick = connect,
            modifier = Modifier.padding(horizontal = 8.dp),
            enabled = connectionState == Disconnected
        ) {
            Text(StringLocale[if (connectionState == Disconnected) STR_START_OLEBO_WEB else STR_LOGIN_OLEBO_WEB])
        }
    }
}