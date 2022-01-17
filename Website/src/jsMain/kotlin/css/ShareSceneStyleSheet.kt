package fr.olebo.sharescene.css

import org.jetbrains.compose.web.css.*

object ShareSceneStyleSheet : StyleSheet() {
    private val mdcThemePrimary by materialVariable<CSSColorValue>()

    init {
        root style {
            mdcThemePrimary(Color.blue)
        }

        desc(
            className("mdc-text-field--focused") + not(className("mdc-text-field--disabled")),
            className("mdc-floating-label")
        ) style {
            color(mdcThemePrimary.value())
        }
    }

    val boxContainer by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
    }

    val rootContainer by style {
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.Center)
        alignItems(AlignItems.Center)
        height(100.percent)
    }

    val mainContainer by style {
        width(400.px)
        height(280.px)
        borderRadius(15.px)
        border {
            style = LineStyle.Solid
            width = 2.px
        }
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
    }

    val formTitle by style {
        textAlign("center")
        fontSize("xx-large".unsafeCast<CSSNumeric>())
        fontWeight("bold")
    }

    val formContent by style {
        margin(horizontal = 5.percent, vertical = auto)
    }

    val materialBottomMargin by style {
        marginBottom(12.px)
        display(DisplayStyle.Grid)
    }

    val oleboCanvasContainer by style {
        width(100.percent)
        height(100.percent)
        position(Position.Absolute)
        top(zero)
        left(zero)
    }

    val validationErrorText by style {
        color(Color.red)
    }

    /*init {
        println(cssRules.joinToString("\n") { it.stringPresentation() })
    }*/
}