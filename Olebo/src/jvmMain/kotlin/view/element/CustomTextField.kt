package jdr.exia.view.element

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import jdr.exia.view.ui.isDarkTheme

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    focused: Boolean = false
) {
    val focusRequester = remember { FocusRequester() }

    TextField(
        value,
        onValueChange,
        modifier = modifier.focusRequester(focusRequester),
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

    LaunchedEffect(focused) {
        if (focused)
            focusRequester.requestFocus()
    }
}

private val unfocusedColor: Color
    @Composable
    @ReadOnlyComposable
    get() {
        val color = if (isDarkTheme) Color.LightGray else Color.DarkGray
        return color.copy(alpha = TextFieldDefaults.UnfocusedIndicatorLineOpacity)
    }