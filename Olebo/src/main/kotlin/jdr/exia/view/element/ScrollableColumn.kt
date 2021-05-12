@file:Suppress("FunctionName")

package jdr.exia.view.element

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

object ScrollableColumnScope : ColumnScope {
    @Composable
    fun <T> ColumnItem(items: List<T>, content: @Composable (T) -> Unit) = items.forEach { item ->
        content(item)
    }
}

@Composable
fun ScrollableColumn(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ScrollableColumnScope.() -> Unit
) = Box(modifier = modifier) {
    Box(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        Column {
            with(ScrollableColumnScope) { content() }
        }
    }

    VerticalScrollbar(
        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        adapter = rememberScrollbarAdapter(scrollState)
    )
}

