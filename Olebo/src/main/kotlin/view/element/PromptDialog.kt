package jdr.exia.view.element

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import jdr.exia.view.element.builder.ComposableContentBuilder
import jdr.exia.view.element.builder.ContentBuilder
import jdr.exia.view.element.builder.ContentButtonBuilder
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.tools.withHandCursor

@Composable
fun PromptDialog(
    visible: Boolean,
    title: String,
    message: String,
    onCloseRequest: DefaultFunction,
    buttonBuilders: List<ContentBuilder>,
    width: Dp = 400.dp
) {
    if (visible) {
        val state = rememberDialogState(size = WindowSize(width, 150.dp))

        Dialog(onCloseRequest = onCloseRequest, title = title, resizable = false, state = state) {
            this.window.isModal = true

            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround) {
                Text(text = message, modifier = Modifier.fillMaxWidth().padding(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    buttonBuilders.forEach {
                        OutlinedButton(onClick = it.onChange, modifier = Modifier.withHandCursor()) {
                            when (it) {
                                is ContentButtonBuilder -> Text(text = it.content)
                                is ComposableContentBuilder -> it.content()
                                else -> TODO("Button type ${it::class.simpleName} are not implemented.")
                            }
                        }
                    }
                }
            }
        }
    }
}