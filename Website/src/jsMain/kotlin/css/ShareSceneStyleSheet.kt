package fr.olebo.sharescene.css

import org.jetbrains.compose.web.css.*

object ShareSceneStyleSheet : StyleSheet() {
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
        flexFlow(FlexDirection.Column)
    }

    val formTitle by style {
        textAlign("center")
        fontSize("xx-large".unsafeCast<CSSNumeric>())
        fontWeight("bold")
    }

    val formContent by style {
        margin(horizontal = 5.percent)
    }
}