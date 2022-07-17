package jdr.exia.update

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.TrayState

val trayState = TrayState()

var trayHint by mutableStateOf("")

@Composable
fun ApplicationScope.Tray(): Unit = Tray(icon = UpdateTrayIcon, state = trayState, tooltip = trayHint)