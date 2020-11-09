import utils.checkForUpdate
import view.homeFrame.HomeFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

const val VERSION = "1.2.0-BETA"

const val DEBUG = false

fun main() {
    if (!DEBUG)
        checkForUpdate()
    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
        )

        HomeFrame().isVisible = true
    }
}
