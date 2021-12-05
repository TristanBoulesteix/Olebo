package fr.olebo.sharescene.components

import androidx.compose.runtime.Composable
import fr.olebo.sharescene.css.OleboStyleSheet
import fr.olebo.sharescene.css.classes
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text

@Composable
fun TitledInputText(
    label: String,
    input: String,
    onInputChange: (String) -> Unit
) = Div(attrs = classes(OleboStyleSheet.alignedForm)) {
    Text(label)

    Input(type = InputType.Text) {
        value(input)
        onInput { onInputChange(it.value) }
    }
}