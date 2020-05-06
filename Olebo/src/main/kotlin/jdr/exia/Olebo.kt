package jdr.exia

import javafx.stage.Stage
import jdr.exia.utils.HttpClientUpdater
import jdr.exia.view.homeFrame.HomeFrame2
import jdr.exia.view.style.CommonStyle
import tornadofx.*
import javax.swing.SwingUtilities
import javax.swing.UIManager

const val VERSION = "1.1.0-BETA"

class Olebo : App(HomeFrame2::class, CommonStyle::class) {
    init {
        reloadStylesheetsOnFocus()
    }

    override fun start(stage: Stage) {
        HttpClientUpdater.checkForUpdate()

        SwingUtilities.invokeLater {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            )
        }

        stage.minHeight = 800.0
        stage.minWidth = 600.0

        super.start(stage)
    }
}
