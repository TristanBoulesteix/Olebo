@file:Suppress("FunctionName")

package jdr.exia.view.composable.master

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jdr.exia.localization.*
import jdr.exia.model.element.Element
import jdr.exia.model.tools.withSetter
import jdr.exia.view.element.CustomTextField


@Composable
fun SelectedEditor(modifier: Modifier, selectedElements: List<Element>) =
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        ImagePreview(selectedElements)

        Column(
            modifier = Modifier.padding(start = 5.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            NameElement(selectedElements)
            LabelField(selectedElements)
        }

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

@Composable
private fun NameElement(selectedElements: List<Element>) {
    val text = when {
        selectedElements.isEmpty() -> StringLocale[STR_NO_SELECTED_ELEMENT]
        selectedElements.size == 1 -> selectedElements.first().name
        else -> "${selectedElements.size} ${StringLocale[STR_SELECTED_ELEMENTS, StringStates.NORMAL]}"
    }

    Text(text)
}

@Composable
private fun LabelField(selectedElements: List<Element>) {
    var value by remember(selectedElements.size, selectedElements.firstOrNull()) {
        mutableStateOf(if (selectedElements.size == 1) selectedElements.first().alias else "") withSetter {
            selectedElements.firstOrNull()?.alias = it
        }
    }

    CustomTextField(
        value = value,
        onValueChange = {
            if (selectedElements.size == 1)
                value = it
        },
        placeholder = StringLocale[STR_LABEL]
    )
}