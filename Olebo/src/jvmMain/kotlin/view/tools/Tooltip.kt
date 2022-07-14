package jdr.exia.view.tools

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import jdr.exia.view.element.dialog.DialogManager
import jdr.exia.view.ui.isDarkTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxWithTooltipIfNotNull(
    tooltip: String? = null,
    modifier: Modifier = Modifier,
    tooltipAlignment: Alignment = Alignment.BottomEnd,
    content: @Composable BoxScope.() -> Unit
) = if (tooltip != null && !DialogManager.areDialogVisible) TooltipArea(
    tooltip = {
        Surface(
            modifier = Modifier.shadow(4.dp),
            color = if (isDarkTheme) Color.Gray else Color(255, 255, 210),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = tooltip,
                modifier = Modifier.padding(10.dp)
            )
        }
    },
    modifier = modifier,
    tooltipPlacement = TooltipPlacement.CursorPoint(
        offset = DpOffset(0.dp, 16.dp),
        alignment = tooltipAlignment
    ),
    content = { Box(modifier = modifier, contentAlignment = Alignment.CenterStart, content = content) }
) else Box(modifier = modifier, contentAlignment = Alignment.CenterStart, content = content)