package jdr.exia.view.tools

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxWithTooltipIfNotNull(
    tooltip: String? = null,
    content: @Composable () -> Unit
) = if (tooltip != null) TooltipArea(
    tooltip = {
        Surface(
            modifier = Modifier.shadow(4.dp),
            color = Color(255, 255, 210),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = tooltip,
                modifier = Modifier.padding(10.dp)
            )
        }
    },
    modifier = Modifier.fillMaxSize(),
    tooltipPlacement = TooltipPlacement.CursorPoint(
        offset = DpOffset(0.dp, 16.dp)
    ), content = {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            content()
        }
    }
) else content()