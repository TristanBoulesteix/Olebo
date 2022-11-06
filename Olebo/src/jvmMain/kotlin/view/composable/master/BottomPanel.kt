package jdr.exia.view.composable.master

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jdr.exia.localization.STR_EDIT_TAB
import jdr.exia.localization.STR_OLEBO_WEB_TAB
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get
import jdr.exia.view.component.HeaderTabOptions
import jdr.exia.view.component.HeaderTabPosition
import jdr.exia.view.component.HeaderTabSize
import jdr.exia.view.component.TabPanel

@Composable
fun BottomPanel(
    modifier: Modifier,
    selectedEditor: @Composable () -> Unit,
    shareScene: @Composable () -> Unit
) = Surface(modifier) {
    val tabs = remember { BottomTab.values().toList() }

    TabPanel(
        tabs = tabs,
        tabNameProvider = BottomTab::tabName,
        headerTabOption = HeaderTabOptions(
            paddingHeight = 1.dp,
            tabSize = HeaderTabSize.Small,
            tabPosition = HeaderTabPosition.Left
        ),
        content = { currentTab, padding ->
            Surface(modifier = Modifier.padding(padding)) {
                when (currentTab) {
                    BottomTab.Select -> selectedEditor()
                    BottomTab.Web -> shareScene()
                }
            }
        }
    )
}

private enum class BottomTab(val tabName: String) {
    Select(StringLocale[STR_EDIT_TAB]), Web(StringLocale[STR_OLEBO_WEB_TAB])
}