@file:Suppress("FunctionName")

package jdr.exia.view.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import jdr.exia.view.compose.components.HeaderRow
import jdr.exia.view.compose.tools.DefaultFunction
import jdr.exia.viewModel.ElementsEditorViewModel

@Composable
fun ElementsView(onDone: DefaultFunction) = Column {
    val viewModel = remember { ElementsEditorViewModel() }

    HeaderRow {
        TabRow(
            selectedTabIndex = viewModel.currentTabIndex
        ) {

        }
    }
}