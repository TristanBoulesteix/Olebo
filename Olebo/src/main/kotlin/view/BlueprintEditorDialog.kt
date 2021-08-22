package jdr.exia.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import jdr.exia.localization.STR_CLOSE
import jdr.exia.localization.STR_MANAGE_BLUEPRINTS
import jdr.exia.localization.StringLocale
import jdr.exia.view.composable.editor.ElementsView
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.ui.HOME_WINDOWS_SIZE
import java.awt.Dimension

@Composable
fun BlueprintEditorDialog(onCloseRequest: DefaultFunction) {
    val state = rememberDialogState(size = HOME_WINDOWS_SIZE)

    Dialog(onCloseRequest = onCloseRequest, title = StringLocale[STR_MANAGE_BLUEPRINTS], state = state) {
        LaunchedEffect(Unit) {
            HOME_WINDOWS_SIZE.let { (width, height) ->
                window.minimumSize = Dimension(width.value.toInt(), height.value.toInt())
            }
        }

        ElementsView(onDone = onCloseRequest, closeText = StringLocale[STR_CLOSE])
    }
}