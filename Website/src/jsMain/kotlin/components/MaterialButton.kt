package fr.olebo.sharescene.components

import androidx.compose.runtime.Composable
import dev.petuska.kmdc.button.MDCButton
import dev.petuska.kmdc.button.MDCButtonOpts.Type
import org.jetbrains.compose.web.attributes.disabled

@Composable
fun MaterialButton(
    text: String,
    enabled: Boolean = true,
    onclick: () -> Unit
) = MDCButton(text = text, opts = { type = Type.Outlined }) {
    onClick { onclick() }

    if (enabled)
        disabled()
}