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
import jdr.exia.view.compose.tools.BorderInlined
import jdr.exia.view.compose.tools.DefaultFunction
import jdr.exia.view.compose.tools.applyIf
import jdr.exia.view.compose.tools.border
import jdr.exia.view.compose.ui.typography

@Composable
fun ContentRow(
    content: @Composable DefaultFunction,
    onClick: DefaultFunction? = null,
    modifier: Modifier = Modifier,
    buttonBuilders: List<ButtonBuilder> = emptyList(),
    removeBottomBorder: Boolean = false
) = Row(
    modifier = modifier.fillMaxWidth().size(65.dp)
        .applyIf(condition = !removeBottomBorder, mod = { border(bottom = BorderInlined.defaultBorder) }),
    horizontalArrangement = Arrangement.End
) {
    var boxModifier = Modifier.fillMaxHeight().weight(1f, fill = true)

    if (onClick != null)
        boxModifier = boxModifier.clickable(onClick = onClick)

    Box(modifier = boxModifier, contentAlignment = Alignment.CenterStart) { content() }

    buttonBuilders.forEach { (icon, action) ->
        RowButton(
            image = icon,
            onClick = action,
            modifier = Modifier.applyIf(
                condition = removeBottomBorder,
                mod = { border(bottom = BorderInlined.defaultBorder) })
        )
    }
}

@Composable
fun ContentRow(
    contentText: String,
    onClick: DefaultFunction? = null,
    modifier: Modifier = Modifier,
    buttonBuilders: List<ButtonBuilder> = emptyList()
) = ContentRow(
    content = { Text(text = contentText, style = typography.h1, modifier = Modifier.padding(10.dp)) },
    onClick = onClick,
    modifier = modifier,
    buttonBuilders = buttonBuilders
)

data class ButtonBuilder(val icon: ImageBitmap, val onClick: () -> Unit)

@Composable
private fun RowButton(image: ImageBitmap, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.size(65.dp)
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