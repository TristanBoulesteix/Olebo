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
import jdr.exia.model.element.Size
import jdr.exia.model.tools.toMutableState
import jdr.exia.view.element.CustomTextField
import jdr.exia.view.element.TitledDropdownMenu
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.tools.withFocusCursor

@Composable
fun SelectedEditor(modifier: Modifier, selectedElements: List<Element>, repaint: DefaultFunction) =
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        ImagePreview(selectedElements)

        ColumnEditor {
            NameElement(selectedElements)
            LabelField(selectedElements, repaint)
        }

        ColumnEditor {
            SizeSelector(selectedElements)
        }
    }

@Composable
fun ColumnEditor(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) = Column(
    modifier = modifier.padding(start = 5.dp).fillMaxHeight(),
    verticalArrangement = Arrangement.SpaceAround,
    content = content
)

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
private fun LabelField(selectedElements: List<Element>, repaint: DefaultFunction) {
    var value by remember(selectedElements.size, selectedElements.firstOrNull()) {
        if (selectedElements.size == 1) {
            selectedElements.first()::alias.toMutableState(repaint)
        } else mutableStateOf("")
    }

    CustomTextField(
        value = value,
        onValueChange = {
            if (selectedElements.size == 1)
                value = it
        },
        placeholder = StringLocale[STR_LABEL],
        modifier = Modifier.withFocusCursor()
    )
}

@Composable
private fun SizeSelector(selectedElements: List<Element>) {
    var selectedSize by remember { mutableStateOf(Size.DEFAULT) }

    val isEnabled = selectedElements.isNotEmpty()

    TitledDropdownMenu(
        title = StringLocale[STR_SIZE],
        items = Size.values(),
        onValueChanged = { selectedSize = it },
        selectedItem = selectedSize,
        isEnabled = isEnabled
    )
}