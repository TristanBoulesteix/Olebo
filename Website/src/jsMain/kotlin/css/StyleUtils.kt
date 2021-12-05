package fr.olebo.sharescene.css

import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.w3c.dom.HTMLDivElement

fun classes(vararg classes: String): AttrBuilderContext<HTMLDivElement> = { classes(*classes) }

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

fun StyleBuilder.flexFlow(flexDirection: FlexDirection) = property(
    "flex-flow",
    flexDirection.value
)