package jdr.exia.view.element.dialog

import androidx.compose.runtime.Composable

@Composable
fun MessageDialog(visible: Boolean = true, title: String, message: String) =
    PromptDialog(visible = visible, title = title, message = message, onCloseRequest = {}, buttonBuilders = emptyList())