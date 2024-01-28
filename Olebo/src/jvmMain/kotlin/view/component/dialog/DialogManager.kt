package jdr.exia.view.component.dialog

import androidx.compose.runtime.*

/**
 * TODO: Remove this when crash for tooltips is fixed
 */
object DialogManager {
    var dialogVisibleNum by mutableIntStateOf(0)

    val areDialogVisible by derivedStateOf { dialogVisibleNum > 0 }
}