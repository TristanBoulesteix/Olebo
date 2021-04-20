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
import jdr.exia.view.compose.tools.BorderBuilder
import jdr.exia.view.compose.tools.DefaultFunction
import jdr.exia.view.compose.tools.applyIf
import jdr.exia.view.compose.tools.border
import jdr.exia.view.compose.ui.typography

@Composable
fun ContentListRow(
    content: @Composable DefaultFunction,
    onClick: DefaultFunction? = null,
    modifier: Modifier = Modifier,
    buttonBuilders: List<ButtonBuilder> = emptyList(),
    removeBottomBorder: Boolean = false
) = Row(
    modifier = modifier.fillMaxWidth().size(65.dp)
        .applyIf(condition = !removeBottomBorder, mod = { border(bottom = BorderBuilder.defaultBorder) }),
    horizontalArrangement = Arrangement.End
) {
    var boxModifier = Modifier.fillMaxHeight().weight(1f, fill = true)

    if (onClick != null)
        boxModifier = boxModifier.clickable(onClick = onClick)

    Box(modifier = boxModifier, contentAlignment = Alignment.CenterStart) { content() }

    buttonBuilders.forEach { (content, isEnabled, action) ->
        RowButton(
            content = content,
            onClick = action,
            clickEnabled = isEnabled,
            modifier = Modifier.applyIf(
                condition = removeBottomBorder,
                mod = { border(bottom = BorderBuilder.defaultBorder) })
        )
    }
}

@Composable
fun ContentListRow(
    contentText: String,
    onClick: DefaultFunction? = null,
    modifier: Modifier = Modifier,
    buttonBuilders: List<ButtonBuilder> = emptyList()
) = ContentListRow(
    content = { ContentText(contentText) },
    onClick = onClick,
    modifier = modifier,
    buttonBuilders = buttonBuilders
)

@Composable
fun ContentText(contentText: String) =
    Text(text = contentText, style = typography.h1, modifier = Modifier.padding(10.dp))

data class ButtonBuilder(val content: Any, val clickEnabled: Boolean = true, val onClick: DefaultFunction) {
    constructor(icon: ImageBitmap, clickEnabled: Boolean = true, onClick: DefaultFunction) : this(
        content = icon,
        clickEnabled = clickEnabled,
        onClick = onClick
    )

    constructor(icon: ImageBitmap) : this(icon, clickEnabled = false, onClick = {})

    constructor(content: Any) : this(content = content, clickEnabled = false, onClick = {})

    constructor() : this(content = "")
}

@Composable
private fun RowButton(content: Any, modifier: Modifier, onClick: DefaultFunction, clickEnabled: Boolean) {
    Box(
        modifier = modifier.size(65.dp)
            .border(start = BorderBuilder.defaultBorder)
            .clickable(onClick = onClick, enabled = clickEnabled),
        contentAlignment = Alignment.CenterStart
    ) {
        if (content is ImageBitmap) {
            Image(
                bitmap = content,
                contentDescription = "button",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Text(text = content.toString(), modifier = Modifier.align(Alignment.Center))
        }
    }
}