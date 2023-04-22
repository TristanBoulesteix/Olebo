package jdr.exia.view.component.form

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import jdr.exia.SimpleFunction
import jdr.exia.view.tools.BoxWithTooltipIfNotNull

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextTrailingIcon(icon: ImageVector, tooltipMessage: String? = null, onClick: SimpleFunction? = null) =
    BoxWithTooltipIfNotNull(
        tooltip = tooltipMessage,
        modifier = Modifier.clickable { onClick?.invoke() }
    ) {
        Icon(
            imageVector = icon,
            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
            contentDescription = null
        )
    }