package jdr.exia.view

import androidx.compose.desktop.ComposePanel
import androidx.compose.desktop.SwingPanel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jdr.exia.model.act.Act
import jdr.exia.view.composable.master.ItemList
import jdr.exia.view.ui.DIMENSION_FRAME
import jdr.exia.view.ui.setThemedContent
import jdr.exia.viewModel.MainViewModel
import javax.swing.JFrame

class MasterWindow(act: Act) : JFrame() {
    private val viewModel: MainViewModel =
        MainViewModel(
            act = act,
            closeMasterWindow = this::dispose,
            focusMasterWindow = this::requestFocus,
            getMasterWindowScreen = { this.graphicsConfiguration.device }
        )

    init {
        // Initilize content frame
        this.extendedState = MAXIMIZED_BOTH
        this.size = DIMENSION_FRAME
        this.isFocusable = true
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.jMenuBar = viewModel.menuBar

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
        Map()
    }

    @Suppress("FunctionName")
    @Composable
    private fun Map() = SwingPanel(
        modifier = Modifier.fillMaxSize(),
        factory = viewModel::panel::get
    )
}