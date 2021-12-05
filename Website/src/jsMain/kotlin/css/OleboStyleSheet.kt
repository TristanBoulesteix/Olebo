package fr.olebo.sharescene.css

import org.jetbrains.compose.web.css.*

object OleboStyleSheet : StyleSheet() {
    val boxContainer by style {
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        flex(1, 1, "auto".unsafeCast<CSSNumeric>())
    }

    val alignedForm by style {
        width(100.percent)
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.SpaceBetween)
    }
}