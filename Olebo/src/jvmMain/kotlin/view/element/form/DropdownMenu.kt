package jdr.exia.view.element.form

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jdr.exia.model.type.imageFromIconRes

@Composable
fun <T : Any> DropdownMenu(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    selectedContent: @Composable RowScope. (T) -> Unit = { Text(it.toString()) },
    label: String? = null
) = Row(verticalAlignment = Alignment.CenterVertically) {
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
                Image(
                    imageFromIconRes("arrow_dropdown"),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
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
