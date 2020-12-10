import utils.checkForUpdate
import view.frames.home.HomeFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

const val OLEBO_VERSION = "1.6.0-BETA"

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
