@file:Suppress("FunctionName")

package jdr.exia.view.compose.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun IntTextField(
    value: Int?,
    onValueChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
) {
    var text by remember { mutableStateOf(value.toString()) }

    TextField(
        value = text,
        onValueChange = {
            if (it.toIntOrNull() != null || it.isEmpty() || it == "-") {
                text = it
            }
            onValueChange(it.toIntOrNull())
        },
        modifier = modifier,
        colors = colors,
        keyboardOptions = keyboardOptions,
        singleLine = true
    )
}