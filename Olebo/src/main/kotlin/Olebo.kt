import utils.checkForUpdate
import view.frames.home.HomeFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

const val VERSION = "1.4.1-BETA"

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
