package jdr.exia.view.composable.master

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import jdr.exia.localization.*
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.TypeElement
import jdr.exia.model.type.imageFromPath
import jdr.exia.view.component.ContentListRow
import jdr.exia.view.component.LazyScrollableColumn
import jdr.exia.view.component.contentListRow.ImageButtonBuilder
import jdr.exia.view.component.form.LabeledRadioMenuItem
import jdr.exia.view.tools.defaultBorderColor
import jdr.exia.view.ui.backgroundImageColor
import jdr.exia.view.ui.isDarkTheme
import jdr.exia.viewModel.holder.TypedBlueprints
import jdr.exia.viewModel.holder.orEmptyValues
import jdr.exia.viewModel.tags.BlueprintFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun ItemList(
    modifier: Modifier,
    items: Flow<TypedBlueprints?>,
    searchString: String,
    onSearch: suspend (String) -> Unit,
    currentFilter: BlueprintFilter,
    setCurrentFilter: (BlueprintFilter) -> Unit,
    createElement: (Blueprint) -> Unit
) = Surface(
    modifier = modifier.widthIn(max = 450.dp).fillMaxHeight(),
    border = BorderStroke(1.dp, defaultBorderColor)
) {
    val scope = rememberCoroutineScope()

    Column {
        OutlinedTextField(
            value = searchString,
            onValueChange = { scope.launch { onSearch(it) } },
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            placeholder = { Text(text = StringLocale[STR_SEARCH]) },
            singleLine = true,
            trailingIcon = { FilterOptions(currentFilter, setCurrentFilter) }
        )

        val itemsAsState by items.collectAsState(null)

        ItemList(items = itemsAsState, createElement = createElement)
    }
}

@Composable
private fun FilterOptions(currentFilter: BlueprintFilter, setCurrentFilter: (BlueprintFilter) -> Unit) {
    var showFilterOptions by remember { mutableStateOf(false) }

    Icon(
        imageVector = Icons.Outlined.FilterAlt,
        contentDescription = "filter",
        modifier = Modifier.clickable { showFilterOptions = true }.pointerHoverIcon(PointerIcon.Default)
    )

    DropdownMenu(
        expanded = showFilterOptions,
        onDismissRequest = { showFilterOptions = false }
    ) {
        val availableFilters = remember { BlueprintFilter.entries }

        availableFilters.forEach {
            LabeledRadioMenuItem(
                onClick = {
                    showFilterOptions = false
                    setCurrentFilter(it)
                },
                text = it.value,
                selected = currentFilter == it
            )
        }
    }
}

@Composable
private fun ItemList(
    items: TypedBlueprints?,
    createElement: (Blueprint) -> Unit
) = LazyScrollableColumn {
    val isInitialized = items != null

    items.orEmptyValues().forEach { (type, list) ->
        item(type) {
            ContentListRow(
                contentText = type.localizedName,
                modifier = Modifier.background(if (isDarkTheme) Color(26, 29, 102) else Color.Cyan)
            )
        }

        if (list.isEmpty()) {
            item(type to list) {
                ContentListRow(
                    contentText = if (isInitialized) StringLocale[STR_NO_ELEMENT] else "${StringLocale[STR_LOADING]}...",
                    enabled = false
                )
            }
        } else {
            items(items = list, key = { it.id }) {
                val focusManager = LocalFocusManager.current

                val name = transaction { it.realName }

                ContentListRow(
                    modifier = Modifier.clickable {
                        createElement(it)
                        focusManager.clearFocus()
                    },
                    contentText = name,
                    buttonBuilders = {
                        ImageButtonBuilder(
                            if (type == TypeElement.Basic) useResource("sprites/${it.sprite}", ::loadImageBitmap)
                            else imageFromPath(it.sprite),
                            backgroundColor = MaterialTheme.colors.backgroundImageColor
                        )
                    }
                )
            }
        }
    }
}