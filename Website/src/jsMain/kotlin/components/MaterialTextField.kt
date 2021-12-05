package fr.olebo.sharescene.components

import androidx.compose.runtime.Composable
import fr.olebo.sharescene.css.MaterialStyleSheet
import fr.olebo.sharescene.css.classes
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun MaterialTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) = Label(attrs = classes(MaterialStyleSheet.materialTextField)) {
    Input(type = InputType.Text) {
        placeholder(" ")
        value(value)
        onInput { onValueChange(it.value) }
    }
    Span { Text(label) }
}