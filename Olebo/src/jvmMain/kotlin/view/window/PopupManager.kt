package jdr.exia.view.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

typealias PopupContent = @Composable PopupContext.() -> Unit

val LocalPopup = staticCompositionLocalOf<PopupManager?> { null }

@Stable
sealed interface PopupManager {
    var content: PopupContent?

    fun close() {
        content = null
    }
}

@Stable
interface PopupContext {
    var backgroundColor: Color

    var shape: Shape

    var fractionContent: Float
}