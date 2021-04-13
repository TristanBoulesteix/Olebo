@file:Suppress("FunctionName")

package jdr.exia.view.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import jdr.exia.model.utils.Result
import jdr.exia.view.compose.components.FooterRow
import jdr.exia.view.compose.components.HeaderRow
import jdr.exia.view.compose.tools.BorderInlined
import jdr.exia.view.compose.tools.DefaultFunction
import jdr.exia.view.compose.tools.applyIf
import jdr.exia.view.compose.tools.border
import jdr.exia.view.compose.ui.blue
import jdr.exia.viewModel.ElementsEditorViewModel

@Composable
fun ElementsView(onDone: DefaultFunction) = Column {
    val viewModel = remember { ElementsEditorViewModel() }

    Scaffold(
        backgroundColor = blue,
        topBar = {
            HeaderRow {
                Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                    viewModel.tabs.forEach { tab ->
                        Text(
                            text = tab.typeName,
                            fontWeight = FontWeight.Bold.takeIf { viewModel.currentTab == tab },
                            modifier = Modifier.applyIf(
                                condition = viewModel.currentTab == tab,
                                mod = { border(bottom = BorderInlined(5.dp, Color.Black)) }
                            ).clickable { viewModel.onSelectTab(tab) }.padding(20.dp)
                        )
                    }
                }
            }
        },
        content = {

        },
        bottomBar = {
            FooterRow(
                lazyResult = lazy { Result.Success },
                onDone = onDone
            )
        }
    )


}