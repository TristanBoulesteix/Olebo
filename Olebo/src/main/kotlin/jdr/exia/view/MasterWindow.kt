package jdr.exia.view

import jdr.exia.model.act.Act
import jdr.exia.view.menubar.MasterMenuBar
import jdr.exia.view.tools.gridBagConstraintsOf
import jdr.exia.view.ui.DIMENSION_FRAME
import jdr.exia.viewModel.MainViewModel
import java.awt.GridBagConstraints
import javax.swing.JFrame

class MasterWindow(act: Act) : JFrame() {
    private val viewModel = MainViewModel(act)

    init {
        // Initilize content frame
        this.extendedState = MAXIMIZED_BOTH
        this.size = DIMENSION_FRAME
        this.isFocusable = true
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.jMenuBar = MasterMenuBar

        // Add content
        val itemConstraints = gridBagConstraintsOf(
            gridx = 0,
            gridy = 0,
            gridHeight = 2,
            weightx = 1.0,
            weighty = 2.0,
            fill = GridBagConstraints.BOTH
        )
        val selectConstraints = gridBagConstraintsOf(
            gridx = 1,
            gridy = 1,
            gridWidth = GridBagConstraints.REMAINDER,
            weightx = 0.5,
            weighty = 1.0,
            fill = GridBagConstraints.BOTH
        )
        val mapConstraints = gridBagConstraintsOf(
            gridx = 3,
            gridy = 0,
            weightx = 3.0,
            weighty = 7.0,
            fill = GridBagConstraints.BOTH
        )
    }
}