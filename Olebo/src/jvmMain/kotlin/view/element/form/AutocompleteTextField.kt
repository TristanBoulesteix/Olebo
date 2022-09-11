package jdr.exia.view.element.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AutocompleteTextField(
    selectedItems: List<String>,
    suggestionsList: List<String>,
    onItemChecked: (valueChecked: String, isChecked: Boolean) -> Unit,
    onItemCreated: (value: String) -> Unit,
    modifier: Modifier
) = Column(modifier) {
    var isExpanded by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    var textValue by remember { mutableStateOf("") }

    val suggestionsListState by rememberUpdatedState(suggestionsList)
    val selectedItemsState by rememberUpdatedState(selectedItems)

    val items by remember {
        derivedStateOf {
            generateSelectableList(textValue, suggestionsListState.sorted(), selectedItemsState)
        }
    }

    TextField(
        modifier = Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
            textFieldSize = coordinates.size.toSize()
        }.onFocusChanged { isExpanded = it.hasFocus }.onKeyEvent {
            if (items.isNotEmpty() && it.key == Key.Enter && textValue.isNotBlank()) {
                val item = items.first()

                if (item.isNew) {
                    onItemCreated(item.value)
                    textValue = ""
                } else {
                    onItemChecked(item.value, !item.isSelected)
                }

                true
            } else {
                false
            }
        },
        value = textValue,
        onValueChange = { textValue = it },
        trailingIcon = {
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.clickable { isExpanded = !isExpanded }
            )
        },
        singleLine = true
    )

    DropdownMenu(
        expanded = isExpanded && items.isNotEmpty(),
        onDismissRequest = { },
        modifier = Modifier.width(
            with(LocalDensity.current) { textFieldSize.width.toDp() }
        ),
        focusable = false
    ) {
        val filteredItems = if (textValue.isNotBlank()) items.filter { textValue in it.value } else items

        filteredItems.forEachIndexed { index, item ->
            key(index) {
                val selection = { isChecked: Boolean ->
                    if (item.isNew) {
                        onItemCreated(item.value)
                        textValue = ""
                    } else {
                        onItemChecked(item.value, isChecked)
                    }
                }

                DropdownMenuItem(onClick = { selection(!item.isSelected) }) {
                    Checkbox(
                        checked = item.isSelected,
                        onCheckedChange = selection
                    )
                    Spacer(Modifier.padding(5.dp))
                    Text(item.value)
                }
            }
        }
    }
}

private fun generateSelectableList(
    textValue: String,
    suggestionsListState: List<String>,
    selectedItemsState: List<String>
) = buildList {
    if (textValue.isNotBlank()) {
        add(SelectableItem(textValue, isNew = true))
    }
    addAll(suggestionsListState.map { SelectableItem(it, it in selectedItemsState) })
}

@Immutable
private data class SelectableItem(val value: String, val isSelected: Boolean = false, val isNew: Boolean = false)
