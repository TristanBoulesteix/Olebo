package jdr.exia.view.utils.components.templates

import jdr.exia.view.utils.DIMENSION_MENU_FRAME
import jdr.exia.viewModel.observer.Observable
import jdr.exia.viewModel.observer.Observer
import java.awt.BorderLayout
import javax.swing.JFrame

/**
 * Template of all JFrame's menu templates
 */
abstract class JFrameTemplate(title: String) : JFrame(),
    Observer {
    protected abstract val observable: Observable

    init {
        this.title = title
        this.minimumSize = DIMENSION_MENU_FRAME
        this.preferredSize = DIMENSION_MENU_FRAME
        this.defaultCloseOperation = DISPOSE_ON_CLOSE
        this.setLocationRelativeTo(null)
        this.layout = BorderLayout()
    }

    override fun dispose() {
        observable.observer = null
        super.dispose()
    }
}