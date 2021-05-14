package jdr.exia.view

import androidx.compose.desktop.ComposePanel
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import jdr.exia.model.act.Act
import jdr.exia.view.composable.master.ItemList
import jdr.exia.view.composable.master.Map
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
                MainContent()
            }
        }
    }

    @Suppress("FunctionName")
    @Composable
    fun MainContent() = Row {
        ItemList()
        Map(viewModel.act)
    }
}