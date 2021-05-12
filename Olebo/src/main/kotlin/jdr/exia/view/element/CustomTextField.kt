package jdr.exia.view.element

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Suppress("FunctionName")
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null
) = TextField(
    value,
    onValueChange,
    modifier = modifier,
    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
    singleLine = true,
    placeholder = {
        if (placeholder != null) {
            Text(placeholder)
        }
    }
)