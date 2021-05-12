package jdr.exia.view

import jdr.exia.model.act.Act
import jdr.exia.view.ui.DIMENSION_FRAME
import jdr.exia.viewModel.MainViewModel
import javax.swing.JFrame

class MasterWindow(act: Act) : JFrame() {
    private val viewModel = MainViewModel(act)

    init {
        this.extendedState = MAXIMIZED_BOTH
        this.size = DIMENSION_FRAME
        this.isFocusable = true
        this.defaultCloseOperation = EXIT_ON_CLOSE
    }
}