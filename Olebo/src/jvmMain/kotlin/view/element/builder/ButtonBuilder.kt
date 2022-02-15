package jdr.exia.view.element.builder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap

sealed interface ContentBuilder {
    val content: Any?

    val tooltip: String?

    val enabled: Boolean

    val onChange: () -> Unit
}

object EmptyContent : ContentBuilder {
    override val content: Nothing? = null

    override val tooltip: Nothing? = null

    override val enabled = false

    override val onChange = {}
}

@Immutable
data class ImageButtonBuilder(
    override val content: ImageBitmap,
    override val tooltip: String? = null,
    override val enabled: Boolean = true,
    val tinted: Boolean = true,
    private val onClick: () -> Unit
) : ContentBuilder {
    override val onChange by ::onClick

    constructor(content: ImageBitmap) : this(content, tinted = false, enabled = false, onClick = {})
}

@Immutable
data class ContentButtonBuilder(
    override val content: String,
    override val tooltip: String? = null,
    override val enabled: Boolean = true,
    private val onClick: () -> Unit = {}
) : ContentBuilder {
    override val onChange by ::onClick

    constructor(content: Any, enabled: Boolean = false) : this(content = content.toString(), enabled = enabled)
}

@Immutable
data class ComposableContentBuilder(
    override val tooltip: String? = null,
    override val content: @Composable () -> Unit
) : ContentBuilder {
    override val enabled = false

    override val onChange = {}
}