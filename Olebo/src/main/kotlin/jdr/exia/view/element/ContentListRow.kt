@file:Suppress("FunctionName")

package jdr.exia.view.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import jdr.exia.view.tools.BorderBuilder
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.tools.applyIf
import jdr.exia.view.tools.border
import jdr.exia.view.ui.typography

@Composable
fun ContentListRow(
    content: @Composable DefaultFunction,
    onClick: DefaultFunction? = null,
    modifier: Modifier = Modifier,
    buttonBuilders: List<ContentBuilder> = emptyList(),
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

    buttonBuilders.forEach {
        RowButton(
            contentBuilder = it,
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
    buttonBuilders: List<ContentBuilder> = emptyList()
) = ContentListRow(
    content = { ContentText(contentText) },
    onClick = onClick,
    modifier = modifier,
    buttonBuilders = buttonBuilders
)

@Composable
fun ContentText(contentText: String) =
    Text(text = contentText, style = typography.h1, modifier = Modifier.padding(10.dp))

interface ContentBuilder {
    val content: Any?

    val enabled: Boolean

    val onChange: DefaultFunction
}

object EmptyContent : ContentBuilder {
    override val content: Nothing? = null

    override val enabled = false

    override val onChange = {}
}

@Stable
data class ImageButtonBuilder(
    override val content: ImageBitmap,
    override val enabled: Boolean = true,
    val onClick: DefaultFunction
) : ContentBuilder {
    override val onChange by ::onClick

    constructor(content: ImageBitmap) : this(content, enabled = false, onClick = {})
}

data class ContentButtonBuilder(
    override val content: String,
    override val enabled: Boolean = false,
    val onClick: DefaultFunction = {}
) : ContentBuilder {
    override val onChange by ::onClick

    constructor(content: Any) : this(content = content.toString())
}

data class ComposableContentBuilder(
    override val content: @Composable DefaultFunction
) : ContentBuilder {
    override val enabled = true

    override val onChange = {}
}

@Composable
private fun RowButton(contentBuilder: ContentBuilder, modifier: Modifier) {
    Box(
        modifier = modifier.size(65.dp)
            .border(start = BorderBuilder.defaultBorder)
            .clickable(onClick = contentBuilder.onChange, enabled = contentBuilder.enabled),
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