@file:Suppress("FunctionName")

package jdr.exia.view.composable.master

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jdr.exia.model.element.Element


@Composable
fun SelectedEditor(modifier: Modifier, selectedElements: List<Element>) =
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        ImagePreview(selectedElements)
    }

@Composable
private fun ImagePreview(selectedElements: List<Element>) {
    val borderColor = if (selectedElements.all { it.isVisible }) Color.Black else Color.Blue

    val modifier = Modifier.padding(15.dp).size(150.dp).border(BorderStroke(10.dp, borderColor))

    if (selectedElements.size == 1) {
        Image(
            bitmap = selectedElements[0].spriteBitmap,
            contentDescription = null,
            modifier = modifier
        )
    } else {
        Spacer(modifier = modifier)
    }
}