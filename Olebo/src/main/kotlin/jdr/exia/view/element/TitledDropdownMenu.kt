@file:Suppress("FunctionName")

package jdr.exia.view.element

import androidx.compose.desktop.SwingPanel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import javax.swing.JComboBox

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> TitledDropdownMenu(
    title: String,
    items: Array<T>,
    selectedItem: T,
    onValueChanged: (T) -> Unit,
    isEnabled: Boolean
) = Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.width(180.dp)
) {
    Text(title)

    SwingPanel(
        factory = {
            JComboBox(items).apply {
                this.addActionListener {
                    if (this.isFocusOwner)
                        onValueChanged(this.selectedItem as T)
                }
            }
        },
        modifier = Modifier.size(width = 120.dp, height = 30.dp),
        update = {
            it.selectedItem = selectedItem
            it.isEnabled = isEnabled
        }
    )
}