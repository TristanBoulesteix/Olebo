package fr.olebo.sharescene.components

import androidx.compose.runtime.Composable
import dev.petuska.kmdc.textfield.MDCTextField
import dev.petuska.kmdc.textfield.MDCTextFieldCommonOpts
import fr.olebo.sharescene.css.ShareSceneStyleSheet
import fr.olebo.sharescene.css.classes
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.events.SyntheticKeyboardEvent
import org.w3c.dom.events.KeyboardEvent

@Composable
fun MaterialTextField(
    label: String,
    value: String,
    id: String? = null,
    onValidation: (SyntheticKeyboardEvent) -> Unit = {},
    onValueChange: (String) -> Unit
) = Div(attrs = classes(ShareSceneStyleSheet.materialBottomMargin)) {
    MDCTextField(
        value,
        opts = {
            this.label = label
            this.type = MDCTextFieldCommonOpts.Type.Outlined
        },
        attrs = {
            onInput { onValueChange(it.value) }

            if (id != null)
                id(id)

            onKeyUp {
                if (it.nativeEvent.unsafeCast<KeyboardEvent>().keyCode == 13) {
                    onValidation(it)
                }
            }
        }
    )
}