package jdr.exia.view.component.contentListRow

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector

typealias ButtonBuilder = @Composable RowButtonScope.() -> Unit

@LayoutScopeMarker
@Immutable
interface RowButtonScope : RowScope {
    @Composable
    fun RowButton(
        tooltip: String?,
        enabled: Boolean,
        backgroundColor: Color,
        onClick: () -> Unit,
        content: @Composable BoxScope.() -> Unit,
    )
}

@Composable
fun RowButtonScope.EmptyContent() = RowButton(
    tooltip = null,
    enabled = false,
    backgroundColor = Color.Transparent,
    onClick = {},
    content = {}
)

@Composable
fun RowButtonScope.ImageButtonBuilder(
    content: ImageBitmap,
    tooltip: String? = null,
    enabled: Boolean = true,
    tinted: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    onClick: () -> Unit
) = RowButton(
    tooltip = tooltip,
    enabled = enabled,
    backgroundColor = backgroundColor,
    onClick = onClick
) {
    Image(
        bitmap = content,
        contentDescription = "button",
        modifier = Modifier.align(Alignment.Center),
        colorFilter = if (tinted) ColorFilter.tint(MaterialTheme.colors.primary) else null
    )
}

@Composable
fun RowButtonScope.ImageButtonBuilder(
    content: ImageBitmap,
    backgroundColor: Color = Color.Transparent
) = ImageButtonBuilder(
    content,
    tinted = false,
    enabled = false,
    backgroundColor = backgroundColor,
    onClick = {}
)

@Composable
fun RowButtonScope.IconButtonBuilder(
    content: ImageVector,
    tooltip: String? = null,
    enabled: Boolean = true,
    tinted: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    onClick: () -> Unit
) = RowButton(
    tooltip = tooltip,
    enabled = enabled,
    backgroundColor = backgroundColor,
    onClick = onClick
) {
    Icon(
        imageVector = content,
        contentDescription = "button",
        modifier = Modifier.align(Alignment.Center),
        tint = if (tinted) MaterialTheme.colors.primary else LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    )
}

@Composable
fun RowButtonScope.ContentButtonBuilder(
    content: String,
    tooltip: String? = null,
    enabled: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    onClick: () -> Unit = {}
) = RowButton(
    tooltip = tooltip,
    enabled = enabled,
    backgroundColor = backgroundColor,
    onClick = onClick
) {
    Text(
        text = content,
        modifier = Modifier.align(Alignment.Center)
    )
}

@Composable
fun RowButtonScope.ContentButtonBuilder(
    content: Any,
    enabled: Boolean = false,
) = ContentButtonBuilder(content.toString(), enabled = enabled)

@Composable
fun RowButtonScope.ComposableContentBuilder(
    tooltip: String? = null,
    backgroundColor: Color = Color.Transparent,
    content: @Composable BoxScope.() -> Unit
) = RowButton(
    content = content,
    tooltip = tooltip,
    enabled = false,
    backgroundColor = backgroundColor,
    onClick = {}
)