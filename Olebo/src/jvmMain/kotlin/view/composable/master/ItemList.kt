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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import jdr.exia.localization.STR_NO_ELEMENT
import jdr.exia.localization.STR_SEARCH
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get
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
import jdr.exia.viewModel.tags.BlueprintFilter
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun ItemList(
    modifier: Modifier,
    items: Map<TypeElement, List<Blueprint>>,
    searchString: String,
    onSearch: (String) -> Unit,
    currentFilter: BlueprintFilter,
    setCurrentFilter: (BlueprintFilter) -> Unit,
    createElement: (Blueprint) -> Unit
) = Surface(
    modifier = modifier.widthIn(max = 450.dp).fillMaxHeight(),
    border = BorderStroke(1.dp, defaultBorderColor)
) {
    Column {
        OutlinedTextField(
            value = searchString,
            onValueChange = onSearch,
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            placeholder = { Text(text = StringLocale[STR_SEARCH]) },
            singleLine = true,
            trailingIcon = { FilterOptions(currentFilter, setCurrentFilter) }
        )

        ItemList(items = items, createElement = createElement)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun FilterOptions(currentFilter: BlueprintFilter, setCurrentFilter: (BlueprintFilter) -> Unit) {
    var showFilterOptions by remember { mutableStateOf(false) }

    Icon(
        imageVector = Icons.Outlined.FilterAlt,
        contentDescription = "filter",
        modifier = Modifier.clickable { showFilterOptions = true }.pointerHoverIcon(PointerIconDefaults.Default)
    )

    DropdownMenu(
        expanded = showFilterOptions,
        onDismissRequest = { showFilterOptions = false }
    ) {
        val availableFilters = remember { BlueprintFilter.values().toList() }

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
    items: Map<TypeElement, List<Blueprint>>,
    createElement: (Blueprint) -> Unit
) = LazyScrollableColumn {
    items.forEach { (type, list) ->
        item(type) {
            ContentListRow(
                contentText = type.localizedName,
                modifier = Modifier.background(if (isDarkTheme) Color(26, 29, 102) else Color.Cyan)
            )
        }

        if (list.isEmpty()) {
            item(type to list) {
                ContentListRow(contentText = StringLocale[STR_NO_ELEMENT], enabled = false)
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