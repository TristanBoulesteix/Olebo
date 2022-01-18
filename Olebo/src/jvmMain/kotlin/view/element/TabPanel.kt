package jdr.exia.view.element

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
    headerTabOption: HeaderTabOptions = HeaderTabOptions(),
    onTabChanged: (T) -> Unit = {},
    content: @Composable (currentTab: T, padding: PaddingValues) -> Unit
) {
    val tabViewModel = remember(tabs) { TabViewModel(tabs, onTabChanged) }

    Scaffold(
        backgroundColor = backgroundColor,
        topBar = {
            HeaderRow(
                backgroundColor = headerTabOption.backgroundColor,
                paddingHeight = headerTabOption.paddingHeight
            ) {
                Row(horizontalArrangement = headerTabOption.tabPosition.horizontalArrangement, modifier = Modifier.fillMaxWidth()) {
                    tabViewModel.tabs.forEach { tab ->
                        Text(
                            text = tab.tabNameProvider(),
                            fontWeight = FontWeight.Bold.takeIf { tabViewModel.currentTab == tab },
                            modifier = Modifier.applyIf(
                                condition = tabViewModel.currentTab == tab,
                                modifier = {
                                    border(
                                        bottom = BorderBuilder(
                                            headerTabOption.tabSize.borderSize,
                                            Color.Black
                                        )
                                    )
                                }
                            ).clickable { tabViewModel.onSelectTab(tab) }
                                .padding(headerTabOption.tabSize.tabPaddingHeight)
                        )
                    }
                }
            }
        },
        content = {
            content(tabViewModel.currentTab, it)
        },
        bottomBar = footer
    )
}

@Immutable
data class HeaderTabOptions(
    val backgroundColor: Color = Color.Unspecified,
    val paddingHeight: Dp = 15.dp,
    val tabSize: HeaderTabSize = HeaderTabSize.Large,
    val tabPosition: HeaderTabPosition = HeaderTabPosition.Center
)

@Immutable
enum class HeaderTabSize(val tabPaddingHeight: Dp, val borderSize: Dp) {
    Small(2.dp, 1.dp),
    Large(20.dp, 5.dp)
}

@Immutable
enum class HeaderTabPosition(val horizontalArrangement: Arrangement.Horizontal) {
    Left(Arrangement.Start),
    Center(Arrangement.SpaceAround)
}