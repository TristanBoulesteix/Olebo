package jdr.exia.view.element

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
    singleLine = true,
    placeholder = {
        if (placeholder != null) {
            Text(placeholder)
        }
    }
)