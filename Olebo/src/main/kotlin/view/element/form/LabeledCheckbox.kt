package jdr.exia.view.element.form

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jdr.exia.view.tools.withHandCursor

@Composable
 fun LabeledCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, label: String) =
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 10.dp).withHandCursor()
        )
        Text(text = label, modifier = Modifier.fillMaxWidth())
    }