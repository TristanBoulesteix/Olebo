package jdr.exia.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jdr.exia.view.tools.applyIf

@Composable
fun HeaderRow(
    backgroundColor: Color = MaterialTheme.colors.secondary,
    paddingHeight: Dp = 15.dp,
    content: @Composable RowScope.() -> Unit
) = Surface(
    modifier = Modifier.fillMaxWidth().applyIf(backgroundColor.isSpecified) { background(backgroundColor) }
        .padding(horizontal = 15.dp, vertical = paddingHeight),
    color = backgroundColor
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        content = content
    )
}