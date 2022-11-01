package jdr.exia.view.element.builder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface ContentBuilder {
    val content: Any?

    val tooltip: String?

    val enabled: Boolean

    val backgroundColor: Color

    val onClick: () -> Unit
}

object EmptyContent : ContentBuilder {
    override val content: Nothing? = null

    override val tooltip: Nothing? = null

    override val enabled = false

    override val backgroundColor = Color.Transparent

    override val onClick = {}
}

@Immutable
data class ImageButtonBuilder(
    override val content: ImageBitmap,
    override val tooltip: String? = null,
    override val enabled: Boolean = true,
    val tinted: Boolean = true,
    override val backgroundColor: Color = Color.Transparent,
    override val onClick: () -> Unit
) : ContentBuilder {
    constructor(content: ImageBitmap, backgroundColor: Color = Color.Transparent) : this(
        content,
        tinted = false,
        enabled = false,
        backgroundColor = backgroundColor,
        onClick = {}
    )
}

@Immutable
data class IconButtonBuilder(
    override val content: ImageVector,
    override val tooltip: String? = null,
    override val enabled: Boolean = true,
    val tinted: Boolean = true,
    override val backgroundColor: Color = Color.Transparent,
    override val onClick: () -> Unit
) : ContentBuilder

@Immutable
data class ContentButtonBuilder(
    override val content: String,
    override val tooltip: String? = null,
    override val enabled: Boolean = true,
    override val backgroundColor: Color = Color.Transparent,
    override val onClick: () -> Unit = {}
) : ContentBuilder {
    constructor(content: Any, enabled: Boolean = false) : this(content = content.toString(), enabled = enabled)
}

@Immutable
data class ComposableContentBuilder(
    override val tooltip: String? = null,
    override val backgroundColor: Color = Color.Transparent,
    override val content: @Composable () -> Unit
) : ContentBuilder {
    override val enabled = false

    override val onClick = {}
}