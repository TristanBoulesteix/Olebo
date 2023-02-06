package jdr.exia.view.component.form

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T : Any> DropdownMenu(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    selectedContent: @Composable RowScope.(T) -> Unit = { Text(it.toString()) },
    label: String? = null
) = Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    if (label != null) {
        Text(label)
        Spacer(Modifier.width(10.dp))
    }

    Box {
        var expanded by remember { mutableStateOf(false) }

        OutlinedButton(onClick = { expanded = !expanded }) {
            Row(modifier = Modifier, horizontalArrangement = Arrangement.SpaceAround) {
                selectedContent(selectedItem)
                Spacer(Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                ) { Text(text = item.toString()) }
            }
        }
    }
}
