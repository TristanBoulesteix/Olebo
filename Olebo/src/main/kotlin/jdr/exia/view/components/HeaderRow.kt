@file:Suppress("FunctionName")

package jdr.exia.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jdr.exia.view.ui.lightOrange

@Composable
inline fun HeaderRow(content: @Composable RowScope.() -> Unit) = Row(
    horizontalArrangement = Arrangement.SpaceAround,
    modifier = Modifier.fillMaxWidth().background(lightOrange).padding(15.dp),
    content = content
)