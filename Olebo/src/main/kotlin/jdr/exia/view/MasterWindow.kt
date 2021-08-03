package jdr.exia.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.awt.SwingPanel
import jdr.exia.localization.ST_STR1_DM_WINDOW_NAME
import jdr.exia.localization.StringLocale
import jdr.exia.model.act.Act
import jdr.exia.view.composable.master.ItemList
import jdr.exia.view.composable.master.SelectedEditor
import jdr.exia.view.tools.event.addKeyPressedListener
import jdr.exia.view.ui.DIMENSION_FRAME
import jdr.exia.view.ui.setThemedContent
import jdr.exia.viewModel.MainViewModel
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.event.KeyEvent

class MasterWindow(act: Act) : ComposableWindow() {
    private val viewModel: MainViewModel = MainViewModel(
        act = act,
        closeMasterWindow = this::dispose,
        focusMasterWindow = this::requestFocus,
        getMasterWindowScreen = { this.graphicsConfiguration.device }
    )

    init {
        // Initialize content frame
        this.extendedState = MAXIMIZED_BOTH
        this.size = DIMENSION_FRAME
        this.minimumSize = DIMENSION_FRAME
        this.isFocusable = true
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.jMenuBar = viewModel.menuBar
        this.title = transaction { act.name }

        this.addKeyPressedListener {
            when (it.keyCode) {
                KeyEvent.VK_UP -> viewModel.select()
                KeyEvent.VK_DOWN -> viewModel.select(false)
                KeyEvent.VK_RIGHT -> viewModel.rotateRight()
                KeyEvent.VK_LEFT -> viewModel.rotateLeft()
            }
        }

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
        ItemList(
            modifier = Modifier.weight(.20f),
            createElement = viewModel::addNewElement,
            items = viewModel.blueprintsGrouped
        )
        Column(modifier = Modifier.weight(.80f).fillMaxSize()) {
            Box(modifier = Modifier.weight(.85f).fillMaxSize()) {
                SwingPanel(factory = viewModel::panel)
            }
            SelectedEditor(
                modifier = Modifier.weight(.15f).fillMaxSize(),
                commandManager = viewModel.commandManager,
                selectedElements = viewModel.selectedElements,
                repaint = viewModel::repaint,
                deleteSelectedElement = viewModel::deleteSelectedElement
            )
        }
    }

    override fun setTitle(title: String) {
        super.setTitle(StringLocale[ST_STR1_DM_WINDOW_NAME, title])
    }

    override fun dispose() {
        viewModel.togglePlayerWindow(false)
        super.dispose()
    }
}