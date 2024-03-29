package jdr.exia.view.element.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import javax.swing.JComboBox

@Suppress("UNCHECKED_CAST")
@Composable
fun <T : Any> TitledDropdownMenu(
    title: String,
    items: Array<T>,
    selectedItem: T,
    onValueChanged: (T) -> Unit,
    isEnabled: Boolean = true
) = Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.width(180.dp)
) {
    Text(title)

    SwingPanel(
        factory = {
            JComboBox(items).apply {
                addActionListener {
                    if (isFocusOwner)
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