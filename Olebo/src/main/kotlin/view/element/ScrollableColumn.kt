@file:Suppress("FunctionName")

package jdr.exia.view.element

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class ScrollableColumnScope(scope: ColumnScope) : ColumnScope by scope {
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
            with(ScrollableColumnScope(this)) { content() }
        }
    }

    VerticalScrollbar(
        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        adapter = rememberScrollbarAdapter(scrollState)
    )
}

@Composable
fun LazyScrollableColumn(
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) = Box(modifier = modifier) {
    LazyColumn(content = content, state = scrollState)
    VerticalScrollbar(
        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        adapter = rememberScrollbarAdapter(scrollState)
    )
}
