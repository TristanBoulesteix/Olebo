package jdr.exia.view.element.dialog

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * TODO: Remove this when crash for tooltips is fixed
 */
object DialogManager {
    var dialogVisibleNum by mutableStateOf(0)

    val areDialogVisible by derivedStateOf { dialogVisibleNum > 0 }
}