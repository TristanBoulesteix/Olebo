@file:Suppress("FunctionName")

package jdr.exia.view.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import jdr.exia.view.compose.DefaultFunction
import jdr.exia.view.compose.ui.BorderInlined
import jdr.exia.view.compose.ui.border
import jdr.exia.view.compose.ui.typography

@Composable
fun ContentRow(
    contentText: String,
    onClick: DefaultFunction? = null,
    modifier: Modifier = Modifier,
    buttonBuilders: List<ButtonBuilder> = emptyList()
) = Row(
    modifier = modifier.fillMaxWidth().size(65.dp).border(bottom = BorderInlined.defaultBorder),
    horizontalArrangement = Arrangement.End
) {
    var boxModifier = Modifier.fillMaxHeight().weight(1f, fill = true)

    if (onClick != null)
        boxModifier = boxModifier.clickable(onClick = onClick)

    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = contentText, style = typography.h1, modifier = Modifier.padding(10.dp))
    }

    buttonBuilders.forEach { (icon, action) ->
        RowButton(image = icon, onClick = action)
    }
}

data class ButtonBuilder(val icon: ImageBitmap, val onClick: () -> Unit)

@Composable
fun RowButton(image: ImageBitmap, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(65.dp)
            .border(start = BorderInlined.defaultBorder)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            bitmap = image,
            contentDescription = "button",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}