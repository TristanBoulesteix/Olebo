package fr.olebo.sharescene.components

import androidx.compose.runtime.Composable
import dev.petuska.kmdc.button.MDCButton
import dev.petuska.kmdc.button.MDCButtonType
import org.jetbrains.compose.web.attributes.disabled

@Composable
fun MaterialButton(
    text: String,
    enabled: Boolean = true,
    id: String? = null,
    onclick: () -> Unit
) = MDCButton(text = text, type = MDCButtonType.Outlined) {
    onClick { onclick() }

    if (!enabled)
        disabled()

    if (id != null)
        id(id)
}