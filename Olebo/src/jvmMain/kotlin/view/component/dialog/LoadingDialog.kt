package jdr.exia.view.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jdr.exia.localization.STR_LOADING
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get

@Composable
fun LoadingDialog(reasonMessage: String = StringLocale[STR_LOADING]) =
    MessageDialog(title = StringLocale[STR_LOADING], onCloseRequest = {}, height = 150.dp) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(reasonMessage)
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }