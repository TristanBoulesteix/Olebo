package jdr.exia.view.element.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import jdr.exia.view.element.builder.ComposableContentBuilder
import jdr.exia.view.element.builder.ContentBuilder
import jdr.exia.view.element.builder.ContentButtonBuilder
import jdr.exia.view.tools.applyIf
import jdr.exia.view.tools.withHandCursor

@Composable
fun PromptDialog(
    title: String,
    message: String,
    onCloseRequest: () -> Unit,
    buttonBuilders: List<ContentBuilder>,
    width: Dp = 400.dp,
    height: Dp = 200.dp,
    visible: Boolean = true,
) = PromptDialog(
    visible = visible,
    title = title,
    onCloseRequest = onCloseRequest,
    buttonBuilders = buttonBuilders,
    width = width,
    height = height,
    content = {
        Text(text = message, modifier = Modifier.fillMaxWidth())
    },
)

@Composable
fun PromptDialog(
    title: String,
    onCloseRequest: () -> Unit,
    buttonBuilders: List<ContentBuilder>,
    width: Dp = 400.dp,
    height: Dp = 200.dp,
    visible: Boolean = true,
    content: @Composable () -> Unit
) {
    if (visible) {
        val state = rememberDialogState(size = WindowSize(width, height))

        Dialog(onCloseRequest = onCloseRequest, title = title, resizable = false, state = state) {
            this.window.isModal = true

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.weight(1f).padding(10.dp).padding(top = 5.dp)) {
                    content()
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    buttonBuilders.forEach {
                        OutlinedButton(
                            onClick = it.onChange,
                            modifier = Modifier.applyIf(condition = it.enabled, modifier = Modifier::withHandCursor),
                            enabled = it.enabled
                        ) {
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