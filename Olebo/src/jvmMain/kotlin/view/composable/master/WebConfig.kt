package jdr.exia.view.composable.master

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jdr.exia.localization.STR_START_OLEBO_WEB
import jdr.exia.localization.ST_OLEBO_WEB_EXPLANATION
import jdr.exia.localization.StringLocale

@Composable
fun WebConfig(connect: () -> Unit) =
    Box(Modifier.padding(5.dp).fillMaxSize(), contentAlignment = Alignment.CenterStart) {
        Row {
            Text(StringLocale[ST_OLEBO_WEB_EXPLANATION])

            Spacer(Modifier.width(4.dp))

            Button(onClick = connect, modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(StringLocale[STR_START_OLEBO_WEB])
            }
        }
    }