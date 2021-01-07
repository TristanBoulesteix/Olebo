package jdr.exia.view.utils.components.templates

import jdr.exia.view.utils.DIMENSION_MENU_FRAME
import jdr.exia.viewModel.pattern.observer.Observable
import jdr.exia.viewModel.pattern.observer.Observer
import java.awt.BorderLayout
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JRootPane
import javax.swing.KeyStroke

/**
 * Template of all JDialog's menu templates.
 *
 * It is similar to JFrameTemplate because I didn't find a public common parent to JDialog and JFrame.
 */
abstract class JDialogTemplate(title: String, modal: Boolean = true) : JDialog(),
    Observer {
    protected abstract val observable: Observable

    init {
        this.title = title
        if (modal) this.modalityType = ModalityType.APPLICATION_MODAL
        this.minimumSize = DIMENSION_MENU_FRAME
        this.preferredSize = DIMENSION_MENU_FRAME
        this.defaultCloseOperation = DISPOSE_ON_CLOSE
        this.setLocationRelativeTo(null)
        this.layout = BorderLayout()
    }

    override fun createRootPane(): JRootPane {
        return super.createRootPane().apply {
            this.registerKeyboardAction(
                { this@JDialogTemplate.dispose() },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
            )
        }
    }

    override fun dispose() {
        observable.observer = null
        super.dispose()
    }
}