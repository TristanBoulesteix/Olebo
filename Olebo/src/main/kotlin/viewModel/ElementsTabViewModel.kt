package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.model.element.TypeElement

class ElementsTabViewModel {
    private var currentTabIndex: Int by mutableStateOf(0)

    val tabs = listOf(TypeElement.Object, TypeElement.PJ, TypeElement.PNJ)

    val currentTab
        get() = tabs.getOrElse(currentTabIndex) { tabs[0] }

    fun onSelectTab(tab: TypeElement) {
        currentTabIndex = tabs.indexOf(tab)
    }
}