package jdr.exia.view.element

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import jdr.exia.view.ui.isDarkTheme

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
    colors = TextFieldDefaults.textFieldColors(
        unfocusedIndicatorColor = unfocusedColor,
        backgroundColor = Color.Transparent
    ),
    placeholder = {
        if (placeholder != null) {
            Text(placeholder)
        }
    }
)

private val unfocusedColor: Color
    @Composable
    @ReadOnlyComposable
    get() {
        val color = if (isDarkTheme) Color.LightGray else Color.DarkGray
        return color.copy(alpha = TextFieldDefaults.UnfocusedIndicatorLineOpacity)
    }