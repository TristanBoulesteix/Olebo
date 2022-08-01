package jdr.exia.view.tools

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp

private const val BULLET = '\u2022'

private val paragraphStyle
    get() = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))

@Stable
fun AnnotatedString.Builder.appendBulletList(vararg listItems: AnnotatedString) = listItems.forEach { item ->
    withStyle(paragraphStyle) {
        append(BULLET)
        append("  ")
        append(item)
        append('\n')
    }
}

val hyperlinkStyle
    get() = SpanStyle(
        color = Color(0xff64B5F6),
        textDecoration = TextDecoration.Underline
    )

@OptIn(ExperimentalTextApi::class)
@Stable
fun annotatedHyperlink(text: String, tag: String, message: String) = buildAnnotatedString {
    withAnnotation(tag = tag, annotation = message) {
        withStyle(hyperlinkStyle) {
            append(text)
        }
    }
}