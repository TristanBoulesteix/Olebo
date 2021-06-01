@file:Suppress("FunctionName")

package jdr.exia.view.element

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measured
import androidx.compose.ui.layout.VerticalAlignmentLine

object ScrollableColumnScope : ColumnScope {
    @Composable
    fun <T> ColumnItem(items: List<T>, content: @Composable (T) -> Unit) = items.forEach { item ->
        content(item)
    }

    override fun Modifier.align(alignment: Alignment.Horizontal): Modifier {
        TODO("Not yet implemented")
    }

    override fun Modifier.alignBy(alignmentLineBlock: (Measured) -> Int): Modifier {
        TODO("Not yet implemented")
    }

    override fun Modifier.alignBy(alignmentLine: VerticalAlignmentLine): Modifier {
        TODO("Not yet implemented")
    }

    override fun Modifier.weight(weight: Float, fill: Boolean): Modifier {
        TODO("Not yet implemented")
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

