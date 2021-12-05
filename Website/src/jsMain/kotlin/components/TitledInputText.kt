package fr.olebo.sharescene.components

import androidx.compose.runtime.Composable
import fr.olebo.sharescene.css.MaterialStyleSheet
import fr.olebo.sharescene.css.ShareSceneStyleSheet
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
) = Div(attrs = classes(ShareSceneStyleSheet.alignedForm)) {
    Text(label)

    Input(type = InputType.Text) {
        classes(MaterialStyleSheet.materialTextField)
        value(input)
        onInput { onInputChange(it.value) }
    }
}