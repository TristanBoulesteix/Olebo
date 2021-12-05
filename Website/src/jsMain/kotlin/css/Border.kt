package fr.olebo.sharescene.css

import org.jetbrains.compose.web.css.*

enum class BorderLocation(private val value: String) {
    Top("top"), Bottom("bottom"), Right("right"), Left("left");

    override fun toString() = value
}

fun StyleBuilder.border(
    style: LineStyle? = null,
    width: CSSLengthValue? = null,
    color: CSSColorValue? = null,
    location: BorderLocation
) = property("border-$location", CSSBorder().apply {
    width?.let { width(it) }
    style?.let { style(it) }
    color?.let { color(it) }
})