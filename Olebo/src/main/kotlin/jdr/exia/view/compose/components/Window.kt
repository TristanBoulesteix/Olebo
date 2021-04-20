@file:Suppress("FunctionName")

package jdr.exia.view.compose.components

import androidx.compose.desktop.ComposePanel
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntSize
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JMenuBar
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

fun CustomWindow(
    title: String = "",
    size: IntSize = IntSize(800, 600),
    minimumSize: IntSize? = null,
    jMenuBar: JMenuBar? = null,
    content: @Composable () -> Unit = { },
) = SwingUtilities.invokeLater {
    JFrame().apply {
        this.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        this.title = title

        this.contentPane = ComposePanel().apply {
            setContent(content)
        }

        this.preferredSize = Dimension(size.width, size.height)
        if (minimumSize != null) {
            this.minimumSize = Dimension(minimumSize.width, minimumSize.height)
        }

        this.jMenuBar = jMenuBar

        this.setLocationRelativeTo(null)
    }.isVisible = true
}