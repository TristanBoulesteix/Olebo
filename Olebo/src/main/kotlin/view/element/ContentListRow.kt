package jdr.exia.view.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jdr.exia.view.element.builder.ComposableContentBuilder
import jdr.exia.view.element.builder.ContentBuilder
import jdr.exia.view.element.builder.ContentButtonBuilder
import jdr.exia.view.element.builder.ImageButtonBuilder
import jdr.exia.view.tools.BorderBuilder
import jdr.exia.view.tools.applyIf
import jdr.exia.view.tools.border
import jdr.exia.view.tools.clickableWithCursor
import jdr.exia.view.ui.typography


@Composable
fun ContentListRow(
    content: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonBuilders: List<ContentBuilder> = emptyList(),
    removeBottomBorder: Boolean = false
) = Row(
    modifier = modifier.fillMaxWidth().size(65.dp)
        .applyIf(condition = !removeBottomBorder, modifier = { border(bottom = BorderBuilder.defaultBorder) }),
    horizontalArrangement = Arrangement.End
) {
    var boxModifier = Modifier.fillMaxHeight().weight(1f, fill = true)

    if (onClick != null)
        boxModifier = boxModifier.clickableWithCursor(enabled = enabled, onClick = onClick)

    Box(modifier = boxModifier, contentAlignment = Alignment.CenterStart) { content() }

    buttonBuilders.forEach {
        RowButton(
            contentBuilder = it,
            modifier = Modifier.applyIf(
                condition = removeBottomBorder,
                modifier = { border(bottom = BorderBuilder.defaultBorder) }
            )
        )
    }
}

@Composable
fun ContentListRow(
    contentText: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonBuilders: List<ContentBuilder> = emptyList()
) = ContentListRow(
    content = { ContentText(contentText = contentText, enabled = enabled) },
    onClick = onClick,
    modifier = modifier,
    buttonBuilders = buttonBuilders,
    enabled = enabled
)

@Composable
fun ContentText(contentText: String, enabled: Boolean = true) =
    Text(
        text = contentText,
        style = typography.h1,
        modifier = Modifier.padding(10.dp),
        color = if (enabled) Color.Unspecified else Color.Gray
    )

@Composable
private fun RowButton(contentBuilder: ContentBuilder, modifier: Modifier) {
    Box(
        modifier = modifier.size(65.dp)
            .border(start = BorderBuilder.defaultBorder)
            .applyIf(condition = contentBuilder.enabled) {
                // We don't use the parameter "enabled" of clickable because it prevents clickable from parent even if disabled
                clickableWithCursor(onClick = contentBuilder.onChange)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        when (contentBuilder) {
            is ImageButtonBuilder -> Image(
                bitmap = contentBuilder.content,
                contentDescription = "button",
                modifier = Modifier.align(Alignment.Center)
            )
            is ContentButtonBuilder -> Text(text = contentBuilder.content, modifier = Modifier.align(Alignment.Center))
            is ComposableContentBuilder -> contentBuilder.content()
            else -> {
                // Do nothing
            }
        }
    }
}