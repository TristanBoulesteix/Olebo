package fr.olebo.sharescene.css

import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.stringPresentation

@Suppress("WrapUnaryOperator")
object MaterialStyleSheet : StyleSheet() {
    @OptIn(ExperimentalComposeWebApi::class)
    val materialTextField by style {
        display(DisplayStyle.InlineBlock)
        overflow("hidden")
        position(Position.Relative)
        lineHeight("1.5")

        child(self, type("input")) style {
            display(DisplayStyle.Block)
            boxSizing("border-box")
            margin(zero)
            border(style = LineStyle.None)
            border(location = BorderLocation.Top, style = LineStyle.Solid, width = 27.px, color = Color.transparent)
            border(location = BorderLocation.Bottom, style = LineStyle.Solid, width = 1.px, color = rgbaMaterial(.6))
            padding(zero, zero, 4.px)
            width(100.percent)
            height(inherit)
            color(rgbaMaterial(.87))
            backgroundColor(Color.transparent)
            lineHeight("inherit")
            property("box-shadow", "none")
            property("caret-color", rgbMaterialPrimary())
            property("transition", "border-bottom 0.2s, background-color 0.2s")
        }

        child(self, adjacent(type("input"), type("span"))) style {
            position(Position.Absolute)
            top(zero)
            left(zero)
            right(zero)
            bottom(zero)
            display(DisplayStyle.Block)
            boxSizing("border-box")
            padding(7.px, zero, zero)
            color(rgbaMaterial(.6))
            fontSize(90.percent)
            lineHeight(18.px)
            property("pointer-events", "none")
            property("transition", "color 0.2s, font-size 0.2s, line-height 0.2s")
        }

        child(self, adjacent(type("input"), type("span") + after)) style {
            content()
            position(Position.Absolute)
            left(zero)
            bottom(zero)
            display(DisplayStyle.Block)
            width(100.percent)
            height(2.px)
            backgroundColor(rgbMaterialPrimary())
            property("transform-origin", "bottom center")
            transform { scaleX(0) }
            property("transition", "transform 0.2s")
        }

        child(self, hover(type("input"))) style {
            property("border-bottom-color", rgbaMaterial(.87))
        }

        child(self, adjacent(type("input") + not(focus) + placeholderShown, type("span"))) style {
            lineHeight(56.px)
        }

        child(self, type("input") + focus) style {
            outline("none")
        }

        child(self, adjacent(type("input") + focus, type("span"))) style {
            color(rgbMaterialPrimary())
        }

        child(self, adjacent(type("input") + focus, type("span") + before)) style {
            opacity(.12)
        }

        child(self, adjacent(type("input") + focus, type("span") + after)) style {
            transform { scale(1) }
        }

        child(self, type("input") + disabled) style {
            property("border-bottom-color", rgbaMaterial(.38))
            color(rgbaMaterial(.38))
        }

        child(self, adjacent(type("input") + disabled, type("span"))) style {
            color(rgbaMaterial(.38))
        }
    }

    @OptIn(ExperimentalComposeWebApi::class)
    val materialButton by style {
        position(Position.Relative)
        display(DisplayStyle.InlineBlock)
        boxSizing("border-box")
        border(width = 1.px, style = LineStyle.Solid, color = rgbaMaterial(.24))
        borderRadius(4.px)
        padding(zero, 16.px)
        minWidth(64.px)
        height(36.px)
        property("vertical-align", "middle")
        textAlign("center")
        property("text-overflow", "ellipsis")
        color(rgbMaterialPrimary())
        backgroundColor(Color.transparent)
        fontSize(14.px)
        fontWeight("500")
        lineHeight(34.px)
        overflow("hidden")
        outline("none")
        cursor("pointer")

        /*self + "::-moz-focus-inner" style {
            border(style = LineStyle.None)
        }*/

        self + before style {
            content()
            position(Position.Absolute)
            top(zero)
            left(zero)
            right(zero)
            bottom(zero)
            backgroundColor("currentColor".unsafeCast<CSSColorValue>())
            opacity(0)
            property("transition", "opacity 0.2s")
        }

        self + after style {
            content()
            position(Position.Absolute)
            left(50.percent)
            top(50.percent)
            borderRadius(50.percent)
            padding(50.percent)
            width(32.px)
            height(32.px)
            backgroundColor("currentColor".unsafeCast<CSSColorValue>())
            opacity(0)
            transform {
                translate(-50.percent, -50.percent)
                scale(1)
            }
            property("transition", "opacity 1s, transform 0.5s")
        }

        self + hover + before style {
            opacity(.04)
        }

        self + focus + before style {
            opacity(.12)
        }

        self + hover + focus + before style {
            opacity(.16)
        }

        self + active + after style {
            opacity(.16)
            transform {
                translate(-50.percent, -50.percent)
                scale(0)
            }
            property("transition", "transform 0s")
        }
    }

    private fun rgbaMaterial(opacity: Number) = rgba(0, 0, 0, opacity)

    private fun rgbMaterialPrimary() = rgb(158, 195, 255)

    init {
        println(cssRules.joinToString("\n") { it.stringPresentation() })
    }
}