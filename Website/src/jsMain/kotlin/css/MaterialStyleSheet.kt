package fr.olebo.sharescene.css

import org.jetbrains.compose.web.css.*

object MaterialStyleSheet : StyleSheet() {
    val materialTextField by style {
        display(DisplayStyle.InlineBlock)
        overflow("hidden")

        child(self, type("input")) style {
            display(DisplayStyle.Block)
            boxSizing("border-box")
            border(location = BorderLocation.Top, style = LineStyle.Solid, width = 27.px, color = Color.transparent)
            border(location = BorderLocation.Bottom, style = LineStyle.Solid, width = 1.px, color = rgbaMaterial(.6))
            padding(zero, zero, 4.px)
            width(100.percent)
            color(rgbaMaterial(.87))
            backgroundColor(Color.transparent)
            property("box-shadow", "none")
            property("caret-color", "rgb(var(--pure-material-primary-rgb, 33, 150, 243))")
            property("transition", "border-bottom 0.2s, background-color 0.2s")
        }

        child(self, type("input") + type("span")) style {
            position(Position.Absolute)
            top(zero)
            left(zero)
            right(zero)
            bottom(zero)
            display(DisplayStyle.Block)
            boxSizing("border-box")
            padding(7.px, zero, zero)
            color(rgbaMaterial(.6))
            fontSize(75.percent)
            lineHeight(18.px)
            property("pointer-events", "none")
            property("transition", "color 0.2s, font-size 0.2s, line-height 0.2s")
        }

        child(self, type("input") + hover())
    }

    private fun rgbaMaterial(opacity: Number) =
        "rgba(var(--pure-material-onsurface-rgb, 0, 0, 0), $opacity)".unsafeCast<CSSColorValue>()
}