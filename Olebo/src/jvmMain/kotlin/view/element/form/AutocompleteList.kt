package jdr.exia.view.element.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import jdr.exia.view.element.LazyScrollableColumn
import jdr.exia.view.tools.BoxWithTooltipIfNotNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AutocompleteList(
    selectedItems: List<String>,
    suggestionsList: List<String>,
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
                .map { SelectableItem(it, it in selectedItemsState) }
                .sortedByDescending(SelectableItem::isSelected)
        }
    }

    val scrollState = rememberLazyListState()

    Surface {
        Column {
            TextField(
                modifier = Modifier.fillMaxWidth().onKeyEvent {
                    if (items.isNotEmpty() && it.key == Key.Enter && textValue.isNotBlank()) {
                        val item = newItem ?: items.firstOrNull() ?: return@onKeyEvent false

                        item.select(
                            isChecked = !item.isSelected,
                            onItemCreated = { itemValue ->
                                coroutineScope.launch {
                                    scrollState.scrollToItem(0)
                                    onItemCreated(itemValue)
                                }
                            },
                            resetTextValue = { textValue = "" },
                            onItemChecked = onItemChecked
                        )

                        true
                    } else {
                        false
                    }
                },
                value = textValue,
                onValueChange = { textValue = it },
                singleLine = true,
                placeholder = { Text("Rechercher ou créer un tag") },
                trailingIcon = { TagTooltip() }
            )

            newItem?.let {
                SelectableRow(
                    item = it,
                    onItemCreated = { itemValue ->
                        coroutineScope.launch {
                            scrollState.scrollToItem(0)
                            onItemCreated(itemValue)
                        }
                    },
                    resetTextValue = { textValue = "" },
                    onItemChecked = onItemChecked
                )
            }
        }
    }

    LazyScrollableColumn(Modifier.fillMaxWidth(), scrollState) {
        items(items, key = SelectableItem::value) { item ->
            SelectableRow(
                item = item,
                onItemCreated = { itemValue ->
                    coroutineScope.launch {
                        scrollState.scrollToItem(0)
                        onItemCreated(itemValue)
                    }
                },
                resetTextValue = { textValue = "" },
                onItemChecked = onItemChecked
            )
        }
    }
}

private val tooltipMessage
    @Stable get() = """
        Vous pouvez associer un élément à un ou plusieurs tags.
        Si un élément et un scénario ou un tag en commun, il est plus facile de les retrouver.
        
        Pour créer un tag, écrivez dans le champ de texte puis appuyez sur la touche "entrer". 
        Pour ajouter associer un tag déjà existant, cocher simplement la case associée.
    """.trimIndent()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TagTooltip() = BoxWithTooltipIfNotNull(
    tooltip = tooltipMessage
) {
    Icon(
        imageVector = Icons.Filled.Info,
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
