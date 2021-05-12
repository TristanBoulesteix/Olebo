package jdr.exia.view

import androidx.compose.desktop.ComposePanel
import jdr.exia.model.act.Act
import jdr.exia.view.composable.master.ItemList
import jdr.exia.view.menubar.MasterMenuBar
import jdr.exia.view.ui.DIMENSION_FRAME
import jdr.exia.view.ui.setThemedContent
import jdr.exia.viewModel.MainViewModel
import javax.swing.JFrame

class MasterWindow(act: Act) : JFrame() {
    private val viewModel = MainViewModel(act)

    init {
        // Initilize content frame
        this.extendedState = MAXIMIZED_BOTH
        this.size = DIMENSION_FRAME
        this.isFocusable = true
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.jMenuBar = MasterMenuBar(act)

        // Add Composable ContentPane
        this.contentPane = ComposePanel().apply {
            setThemedContent {
                ItemList()
            }
        }
    }
}
