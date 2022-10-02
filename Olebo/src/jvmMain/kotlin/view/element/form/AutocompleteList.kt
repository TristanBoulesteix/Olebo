package jdr.exia.view.element.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import jdr.exia.view.element.LazyScrollableColumn
import jdr.exia.view.tools.BoxWithTooltipIfNotNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AutocompleteList(
    selectedItems: List<String>,
    suggestionsList: List<String>,
    placeholder: String,
    tooltipMessage: String,
    onItemChecked: (valueChecked: String, isChecked: Boolean) -> Unit,
    onItemCreated: (value: String) -> Unit,
    modifier: Modifier
) = Column(modifier) {
    val coroutineScope = rememberCoroutineScope()

    var textValue by remember { mutableStateOf("") }

    val suggestionsListState by rememberUpdatedState(suggestionsList)
    val selectedItemsState by rememberUpdatedState(selectedItems)

    val newItem by remember {
        derivedStateOf {
            val shouldCreateANewItem = textValue.isNotBlank() && suggestionsListState.none {
                it.equals(
                    textValue,
                    ignoreCase = true
                )
            }

            if (shouldCreateANewItem) SelectableItem(textValue, isNew = true) else null
        }
    }

    val items by remember {
        derivedStateOf {
            suggestionsListState
                .filter { it.contains(textValue, ignoreCase = true) }
                .sortedByDescending { it == textValue }
                .map { SelectableItem(it, it in selectedItemsState) }
        }
    }

    val scrollState = rememberLazyListState()

    HeaderSearch(
        newItem = newItem,
        coroutineScope = coroutineScope,
        onItemCreated = onItemCreated,
        scrollState = scrollState,
        textValue = textValue,
        onTextValueUpdate = { textValue = it },
        onItemChecked = onItemChecked,
        placeholder = placeholder,
        tooltipMessage = tooltipMessage
    )

    LazyScrollableColumn(Modifier.fillMaxWidth(), scrollState) {
        items(items, key = SelectableItem::value) { item ->
            SelectableRow(
                item = item,
                onItemCreated = { itemValue ->
                    coroutineScope.launch {
                        onItemCreated(itemValue)
                        scrollState.scrollToTop()
                    }
                },
                resetTextValue = { textValue = "" },
                onItemChecked = onItemChecked
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun HeaderSearch(
    newItem: SelectableItem?,
    coroutineScope: CoroutineScope,
    onItemCreated: (value: String) -> Unit,
    scrollState: LazyListState,
    textValue: String,
    onTextValueUpdate: (String) -> Unit,
    onItemChecked: (valueChecked: String, isChecked: Boolean) -> Unit,
    placeholder: String,
    tooltipMessage: String
) = Surface {
    Column {
        TextField(
            modifier = Modifier.fillMaxWidth().onKeyEvent {
                if (it.key == Key.Enter && newItem != null) {
                    newItem.select(
                        isChecked = !newItem.isSelected,
                        onItemCreated = { itemValue ->
                            coroutineScope.launch {
                                onItemCreated(itemValue)
                                scrollState.scrollToTop()
                            }
                        },
                        resetTextValue = { onTextValueUpdate("") },
                        onItemChecked = onItemChecked
                    )

                    true
                } else {
                    false
                }
            },
            value = textValue,
            onValueChange = onTextValueUpdate,
            singleLine = true,
            placeholder = { Text(placeholder) },
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.End),
                    modifier = Modifier.width(70.dp).padding(end = 5.dp)
                ) {
                    if (newItem != null) {
                        TextTrailingIcon(Icons.Outlined.Add) {
                            newItem.select(
                                isChecked = !newItem.isSelected,
                                onItemCreated = { itemValue ->
                                    coroutineScope.launch {
                                        onItemCreated(itemValue)
                                        scrollState.scrollToTop()
                                    }
                                },
                                resetTextValue = { onTextValueUpdate("") },
                                onItemChecked = onItemChecked
                            )
                        }
                    }
                    TextTrailingIcon(Icons.Outlined.Info, tooltipMessage)
                }
            }
        )

        newItem?.let {
            SelectableRow(
                item = it,
                onItemCreated = { itemValue ->
                    coroutineScope.launch {
                        onItemCreated(itemValue)
                        scrollState.scrollToTop()
                    }
                },
                resetTextValue = { onTextValueUpdate("") },
                onItemChecked = onItemChecked
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TextTrailingIcon(icon: ImageVector, tooltipMessage: String? = null, onClick: (() -> Unit)? = null) =
    BoxWithTooltipIfNotNull(
        tooltip = tooltipMessage,
        modifier = Modifier.clickable { onClick?.invoke() }
    ) {
        Icon(
            imageVector = icon,
            modifier = Modifier.pointerHoverIcon(PointerIconDefaults.Hand),
            contentDescription = null
        )
    }

@Composable
private fun SelectableRow(
    item: SelectableItem,
    onItemCreated: (value: String) -> Unit,
    resetTextValue: () -> Unit,
    onItemChecked: (valueChecked: String, isChecked: Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().clickable {
            item.select(!item.isSelected, onItemCreated, resetTextValue, onItemChecked)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isSelected,
            onCheckedChange = {
                item.select(it, onItemCreated, resetTextValue, onItemChecked)
            }
        )
        Spacer(Modifier.padding(5.dp))
        Text(item.value)
    }
}

private suspend fun LazyListState.scrollToTop() {
    delay(100)
    scrollToItem(0)
}

private fun SelectableItem.select(
    isChecked: Boolean,
    onItemCreated: (value: String) -> Unit,
    resetTextValue: () -> Unit,
    onItemChecked: (valueChecked: String, isChecked: Boolean) -> Unit
) {
    if (isNew) {
        onItemCreated(value)
        resetTextValue()
    } else {
        onItemChecked(value, isChecked)
    }
}

@Immutable
private data class SelectableItem(val value: String, val isSelected: Boolean = false, val isNew: Boolean = false)
