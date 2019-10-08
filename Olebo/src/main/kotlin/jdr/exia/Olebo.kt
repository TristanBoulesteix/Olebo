package jdr.exia

import jdr.exia.view.homeFrame.HomeFrame
import javax.swing.UIManager

fun main() {
    UIManager.setLookAndFeel(
        UIManager.getSystemLookAndFeelClassName()
    )
    HomeFrame().isVisible = true
}
