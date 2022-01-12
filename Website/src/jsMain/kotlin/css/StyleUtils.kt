package fr.olebo.sharescene.css

import fr.olebo.sharescene.Base64Image
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.w3c.dom.HTMLElement
import kotlin.properties.ReadOnlyProperty

fun <T : HTMLElement> classes(vararg classes: String): AttrBuilderContext<T> = { classes(*classes) }

inline val auto
    get() = "auto".unsafeCast<CSSNumeric>()

fun StyleBuilder.margin(
    top: CSSNumeric? = null,
    start: CSSNumeric? = null,
    bottom: CSSNumeric? = null,
    end: CSSNumeric? = null
) {
    if (top != null)
        marginTop(top)

    if (start != null)
        marginRight(start)

    if (bottom != null)
        marginBottom(bottom)

    if (end != null)
        marginLeft(end)
}

fun StyleBuilder.margin(horizontal: CSSNumeric? = null, vertical: CSSNumeric? = null) {
    margin(top = vertical, start = horizontal, bottom = vertical, end = horizontal)
}

fun <TValue : StylePropertyValue> materialVariable() = ReadOnlyProperty<Any?, CSSStyleVariable<TValue>> { _, property ->
    CSSStyleVariable(property.name.toKebabCase())
}

private fun String.toKebabCase() = Regex("(?<=[a-zA-Z])[A-Z]").replace(this) { "-${it.value}" }.lowercase()

fun StyleBuilder.backgroundImage(image: Base64Image) = backgroundImage("url('${image.cssBase64ImageCode}')")