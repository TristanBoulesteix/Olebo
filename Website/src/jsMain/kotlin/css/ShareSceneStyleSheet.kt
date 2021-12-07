package fr.olebo.sharescene.css

import org.jetbrains.compose.web.css.*

object ShareSceneStyleSheet : StyleSheet(MaterialStyleSheet) {
    val boxContainer by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
    }

    val alignedForm by style {
        width(100.percent)
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.SpaceBetween)
        margin(bottom = 5.percent)
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
}