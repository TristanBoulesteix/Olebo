package jdr.exia.viewModel.editor

import androidx.compose.runtime.*
import jdr.exia.localization.STR_NEW_ACT
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get

@Stable
class ActCreatorViewModel {
    companion object {
        const val steps = 3
    }

    private var actName: String? by mutableStateOf(null)

    var currentStep by mutableStateOf(1)

    val headerText by derivedStateOf { actName ?: StringLocale[STR_NEW_ACT] }

}