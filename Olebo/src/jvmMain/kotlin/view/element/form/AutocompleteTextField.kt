package jdr.exia.view.element.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

@Composable
fun AutocompleteTextField(
    textValue: String,
    onTextValueChange: (String) -> Unit,
    suggestionsList: List<SelectableItem>,
    modifier: Modifier
) = Column(modifier) {
    var isExpanded by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    TextField(
        modifier = Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
            textFieldSize = coordinates.size.toSize()
        }.onFocusChanged { isExpanded = it.hasFocus },
        value = textValue,
        onValueChange = onTextValueChange,
        trailingIcon = {
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.clickable { isExpanded = !isExpanded }
            )
        }
    )

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { },
        modifier = Modifier.width(
            with(LocalDensity.current) { textFieldSize.width.toDp() }
        ),
        focusable = false
    ) {
        suggestionsList.forEachIndexed { index, item ->
            key(index) {
                DropdownMenuItem(onClick = item::toggleSelection) {
                    Checkbox(checked = item.isSelected, onCheckedChange = { item.isSelected = it })
                    Spacer(Modifier.padding(5.dp))
                    Text(item.value)
                }
            }
        }
    }
}

@Stable
class SelectableItem(val value: String, isSelected: Boolean = false) {
    var isSelected by mutableStateOf(isSelected)

    fun toggleSelection() {
        isSelected = !isSelected
    }
}