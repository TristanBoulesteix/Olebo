package jdr.exia.view.composable.master

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jdr.exia.view.element.HeaderTabOptions
import jdr.exia.view.element.HeaderTabPosition
import jdr.exia.view.element.HeaderTabSize
import jdr.exia.view.element.TabPanel

@Composable
fun BottomPanel(
    modifier: Modifier,
    selectedEditor: @Composable () -> Unit,
    shareScene: @Composable () -> Unit
) = Box(modifier) {
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
            Box(modifier = Modifier.padding(padding)) {
                when (currentTab) {
                    BottomTab.Select -> selectedEditor()
                    BottomTab.Web -> shareScene()
                }
            }
        }
    )
}

private enum class BottomTab(val tabName: String) {
    Select("Selection"), Web("Web (Alpha)")
}