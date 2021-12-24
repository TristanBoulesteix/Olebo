package fr.olebo.sharescene.components

import androidx.compose.runtime.Composable
import dev.petuska.kmdc.textfield.MDCTextField
import dev.petuska.kmdc.textfield.MDCTextFieldCommonOpts
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import org.jetbrains.compose.web.dom.Div

@Composable
fun MaterialTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) = Div(attrs = classes(ShareSceneStyleSheet.materialBottomMargin)) {
    MDCTextField(opts = {
        this.label = label
        this.type = MDCTextFieldCommonOpts.Type.Outlined
    }) {
        value(value)
        onInput { onValueChange(it.value) }
    }
}