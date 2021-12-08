package fr.olebo.sharescene.components

import androidx.compose.runtime.Composable
import fr.olebo.sharescene.css.MaterialStyleSheet
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text

@Composable
fun MaterialButton(content: String, onClick: () -> Unit) = Button(attrs = {
    classes(MaterialStyleSheet.materialButton)
    onClick { onClick() }
}) { Text(content) }