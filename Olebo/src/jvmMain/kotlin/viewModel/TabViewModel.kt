package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

class TabViewModel<T>(val tabs: List<T>, private val onTabChanged: (T) -> Unit) {
    private var currentTabIndex: Int by mutableIntStateOf(0)

    val currentTab
        get() = tabs.getOrElse(currentTabIndex) { tabs[0] }

    fun onSelectTab(tab: T) {
        currentTabIndex = tabs.indexOf(tab)
        onTabChanged(currentTab)
    }
}