package jdr.exia.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.takeOrElse
import jdr.exia.view.component.contentListRow.ButtonBuilder
import jdr.exia.view.component.contentListRow.RowButtonScope
import jdr.exia.view.tools.*
import jdr.exia.view.ui.typography

@Immutable
data class ContentListRowStyle(
    val width: Dp = 65.dp,
    val height: Dp = 65.dp,
    val horizontalPadding: Dp = Dp.Unspecified
)

val LocalContentListRowStyle = compositionLocalOf { ContentListRowStyle() }

@Immutable
private class ContentListRowScope(scope: RowScope, val modifier: Modifier) : RowButtonScope, RowScope by scope {
    @Composable
    override fun RowButton(
        tooltip: String?,
        enabled: Boolean,
        backgroundColor: Color,
        onClick: () -> Unit,
        content: BoxedComposable,
    ) {
        val style = LocalContentListRowStyle.current

        BoxWithTooltipIfNotNull(
            tooltip = tooltip,
            modifier = modifier.size(
                width = style.width - BorderBuilder.defaultBorder.strokeWidth + style.horizontalPadding.takeOrElse(0::dp),
                height = style.height - BorderBuilder.defaultBorder.strokeWidth
            ).background(backgroundColor).border(start = BorderBuilder.defaultBorder).applyIf(condition = enabled) {
                // We don't use the parameter "enabled" of clickable because it prevents clickable from parent even if disabled
                clickable(onClick = onClick)
            }.applyIf(style.horizontalPadding.isSpecified) {
                padding(horizontal = style.horizontalPadding)
            },
            content = content
        )
    }
}

@Composable
fun ContentListRow(
    content: BoxedComposable,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonBuilders: ButtonBuilder = {},
    removeBottomBorder: Boolean = false
) = Row(
    modifier = modifier.fillMaxWidth().size(65.dp)
        .applyIf(condition = !removeBottomBorder, modifier = { border(bottom = BorderBuilder.defaultBorder) }),
    horizontalArrangement = Arrangement.End
) {
    var boxModifier = Modifier.fillMaxHeight().weight(1f, fill = true)

    if (onClick != null)
        boxModifier = boxModifier.clickable(enabled = enabled, onClick = onClick)

    Box(modifier = boxModifier, contentAlignment = Alignment.CenterStart, content = content)

    val buttonsScope = remember(removeBottomBorder) {
        ContentListRowScope(
            scope = this,
            modifier = Modifier.applyIf(
                condition = removeBottomBorder,
                modifier = { composed { border(bottom = BorderBuilder.defaultBorder) } }
            )
        )
    }

    buttonsScope.buttonBuilders()
}

@Composable
fun ContentListRow(
    contentText: String,
    contentTooltip: String? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonBuilders: ButtonBuilder = {}
) = ContentListRow(
    content = {
        BoxWithTooltipIfNotNull(
            tooltip = contentTooltip,
            modifier = Modifier.fillMaxSize()
        ) { ContentText(contentText = contentText, enabled = enabled) }
    },
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
        modifier = Modifier.padding(start = 10.dp),
        color = if (enabled) Color.Unspecified else Color.Gray
    )