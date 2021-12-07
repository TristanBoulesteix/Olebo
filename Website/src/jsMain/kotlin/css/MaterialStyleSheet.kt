package fr.olebo.sharescene.css

import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.stringPresentation

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
            color(rgbaMaterial(.87))
            backgroundColor(Color.transparent)
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

        child(self, type("input") + after) style {
            property("content", "")
            position(Position.Absolute)
            left(zero)
            bottom(zero)
            display(DisplayStyle.Block)
            width(100.percent)
            height(2.px)
            backgroundColor(rgbMaterialPrimary())
            property("transform-origin", "bottom center")
            property("transform", "scaleX(0)")
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

    private fun rgbaMaterial(opacity: Number) = rgba(0, 0, 0, opacity)

    private fun rgbMaterialPrimary() = rgb(33, 150, 243)

    init {
        println(cssRules.joinToString("\n") { it.stringPresentation() })
    }
}