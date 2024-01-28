package jdr.exia.view.component.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import jdr.exia.view.component.contentListRow.ButtonBuilder
import jdr.exia.view.component.contentListRow.ContentButtonBuilder
import jdr.exia.view.component.contentListRow.RowButtonScope
import jdr.exia.view.tools.BoxedComposable
import java.awt.Dialog

@Composable
private fun RowButtonScope.DefaultButton(action: () -> Unit) = ContentButtonBuilder("OK", onClick = action)

@Composable
fun MessageDialog(
    title: String,
    message: String,
    messageLineHeight: TextUnit = TextUnit.Unspecified,
    onCloseRequest: () -> Unit,
    buttonBuilders: ButtonBuilder = { DefaultButton(onCloseRequest) },
    width: Dp = 400.dp,
    height: Dp = 200.dp,
    visible: Boolean = true,
) = MessageDialog(
    visible = visible,
    title = title,
    onCloseRequest = onCloseRequest,
    buttonsBuilder = buttonBuilders,
    width = width,
    height = height,
    content = {
        Text(text = message, modifier = Modifier.fillMaxWidth(), lineHeight = messageLineHeight)
    },
)

@Composable
fun MessageDialog(
    title: String,
    onCloseRequest: () -> Unit,
    buttonsBuilder: ButtonBuilder = {},
    width: Dp = 400.dp,
    height: Dp = 200.dp,
    visible: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    if (visible) {
        val state = rememberDialogState(size = DpSize(width, height))

        DialogWindow(onCloseRequest = onCloseRequest,
            state = state,
            title = title,
            resizable = false,
            onPreviewKeyEvent = {
                if (it.key == Key.Escape || it.key == Key.Enter)
                    onCloseRequest()
                false
            }, content = {
                DisposableEffect(Unit) {
                    window.modalityType = Dialog.ModalityType.APPLICATION_MODAL
                    DialogManager.dialogVisibleNum += 1

                    onDispose {
                        DialogManager.dialogVisibleNum -= 1
                    }
                }

                Card(modifier = Modifier.fillMaxSize()) {
                    Column(
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
                            val scope = remember { DialogRowScope(this) }

                            scope.buttonsBuilder()
                        }
                    }
                }
            })
    }
}

@Immutable
private class DialogRowScope(scope: RowScope) : RowButtonScope, RowScope by scope {
    @Composable
    override fun RowButton(
        tooltip: String?,
        enabled: Boolean,
        backgroundColor: Color,
        onClick: () -> Unit,
        content: BoxedComposable,
    ) = OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Transparent)
    ) {
        Box(content = content)
    }
}