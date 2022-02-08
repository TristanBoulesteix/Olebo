package jdr.exia.view.element

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jdr.exia.view.tools.applyIf

@Composable
inline fun HeaderRow(
    backgroundColor: Color = MaterialTheme.colors.secondary,
    paddingHeight: Dp = 15.dp,
    content: @Composable RowScope.() -> Unit
) = Row(
    horizontalArrangement = Arrangement.SpaceAround,
    modifier = Modifier.fillMaxWidth().applyIf(backgroundColor.isSpecified) { background(backgroundColor) }
        .padding(horizontal = 15.dp, vertical = paddingHeight),
    content = content
)