package jdr.exia.view.element

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import jdr.exia.view.tools.BorderBuilder
import jdr.exia.view.tools.applyIf
import jdr.exia.view.tools.border
import jdr.exia.viewModel.TabViewModel

@Composable
fun <T : Any> TabPanel(
    tabs: List<T>,
    footer: @Composable () -> Unit = {},
    tabNameProvider: T.() -> String = Any::toString,
    backgroundColor: Color = MaterialTheme.colors.background,
    onTabChanged: (T) -> Unit = {},
    content: @Composable (currentTab: T, padding: PaddingValues) -> Unit
) {
    val tabViewModel = remember(tabs) { TabViewModel(tabs, onTabChanged) }

    Scaffold(
        backgroundColor = backgroundColor,
        topBar = {
            HeaderRow {
                Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                    tabViewModel.tabs.forEach { tab ->
                        Text(
                            text = tab.tabNameProvider(),
                            fontWeight = FontWeight.Bold.takeIf { tabViewModel.currentTab == tab },
                            modifier = Modifier.applyIf(
                                condition = tabViewModel.currentTab == tab,
                                modifier = { border(bottom = BorderBuilder(5.dp, Color.Black)) }
                            ).clickable { tabViewModel.onSelectTab(tab) }.padding(20.dp)
                        )
                    }
                }
            }
        }, content = {
            content(tabViewModel.currentTab, it)
        },
        bottomBar = footer
    )
}
