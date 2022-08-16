package jdr.exia.view.element.form

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LabeledTextField(label: String, value: String, onValueChange: (String) -> Unit) = Row {
    Text(label)
    Spacer(Modifier.width(10.dp))
    TextField(value, onValueChange, modifier = Modifier.weight(1f))
}