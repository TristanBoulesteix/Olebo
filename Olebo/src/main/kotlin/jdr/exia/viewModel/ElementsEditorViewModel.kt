package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.model.element.Type

class ElementsEditorViewModel {
    private var currentTabIndex by mutableStateOf(0)

    val tabs = listOf(Type.OBJECT, Type.PJ, Type.PNJ)

    val currentTab
        get() = tabs.getOrElse(currentTabIndex) { tabs[0] }

    fun onSelectTab(tab: Type) {
        currentTabIndex = tabs.indexOf(tab)
    }
}