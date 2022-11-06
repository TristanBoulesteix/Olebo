package jdr.exia.view.component.form

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jdr.exia.SimpleFunction

@Composable
fun LabeledRadioMenuItem(
    onClick: SimpleFunction,
    text: String,
    selected: Boolean
) = DropdownMenuItem(onClick = onClick) {
    RadioButton(selected, onClick, Modifier.size(20.dp).padding(end = 20.dp))

    Text(text)
}