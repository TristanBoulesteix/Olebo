package jdr.exia.view.tools

import androidx.compose.foundation.BoxWithTooltip
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxWithTooltipIfNotNull(
    tooltip: String? = null,
    content: @Composable () -> Unit
) = if (tooltip != null) BoxWithTooltip(
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
    delay = 400,
    contentAlignment = Alignment.CenterStart,
    content = content
) else content()