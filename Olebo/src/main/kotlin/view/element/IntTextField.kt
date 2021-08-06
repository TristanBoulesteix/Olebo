@file:Suppress("FunctionName")

package jdr.exia.view.element

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

private fun String.takeNullable(n: Int?) = when {
    n == null -> this
    this.firstOrNull() == '-' -> this.take(n + 1)
    else -> this.take(n)
}

@Composable
fun IntTextField(
    value: Int?,
    onValueChange: (Int?) -> Unit,
    maxSize: Int? = null,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
) {
    var text by remember { mutableStateOf(value.toString().takeNullable(maxSize)) }

    TextField(
        value = text,
        onValueChange = {
            if (it.toIntOrNull() != null || it.isEmpty() || it == "-") {
                text = it.takeNullable(maxSize)
            }
            onValueChange(it.toIntOrNull())
        },
        modifier = modifier,
        colors = colors,
        keyboardOptions = keyboardOptions,
        singleLine = true
    )
}