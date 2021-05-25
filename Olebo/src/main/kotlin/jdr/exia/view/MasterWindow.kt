package jdr.exia.view

import androidx.compose.desktop.ComposePanel
import androidx.compose.desktop.SwingPanel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jdr.exia.localization.ST_STR1_DM_WINDOW_NAME
import jdr.exia.localization.StringLocale
import jdr.exia.model.act.Act
import jdr.exia.view.composable.master.ItemList
import jdr.exia.view.composable.master.SelectedEditor
import jdr.exia.view.ui.DIMENSION_FRAME
import jdr.exia.view.ui.setThemedContent
import jdr.exia.viewModel.MainViewModel
import org.jetbrains.exposed.sql.transactions.transaction

class MasterWindow(act: Act) : ComposableWindow() {
    private val viewModel: MainViewModel = MainViewModel(
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

        this.title = transaction { act.name }

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
        ItemList(modifier = Modifier.weight(.3f))
        Column(modifier = Modifier.weight(.7f).fillMaxSize()) {
            Map(modifier = Modifier.weight(.85f))
            SelectedEditor(
                modifier = Modifier.weight(.15f).fillMaxSize(),
                commandManager = viewModel.commandManager,
                selectedElements = viewModel.selectedElements,
                repaint = viewModel::repaint,
                deleteSelectedElement = viewModel::deleteSelectedElement
            )
        }
    }

    @Suppress("FunctionName")
    @Composable
    private fun Map(modifier: Modifier) = SwingPanel(
        modifier = modifier.fillMaxSize(),
        factory = viewModel::panel::get
    )

    override fun setTitle(title: String) {
        super.setTitle(StringLocale[ST_STR1_DM_WINDOW_NAME, title])
    }
}