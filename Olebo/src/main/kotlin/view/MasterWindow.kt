package jdr.exia.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPlacement
import jdr.exia.localization.ST_STR1_DM_WINDOW_NAME
import jdr.exia.localization.StringLocale
import jdr.exia.model.act.Act
import jdr.exia.model.dao.option.Settings
import jdr.exia.view.composable.master.ItemList
import jdr.exia.view.composable.master.MapPanel
import jdr.exia.view.composable.master.SelectedEditor
import jdr.exia.view.menubar.MasterMenuBar
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.tools.event.addKeyPressedListener
import jdr.exia.view.ui.MASTER_WINDOW_SIZE
import jdr.exia.viewModel.MasterViewModel
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.event.KeyEvent

@Composable
fun ApplicationScope.MasterWindow(act: Act, onExit: DefaultFunction) {
    val scope = rememberCoroutineScope()

    val viewModel = remember { MasterViewModel(act = act, scope = scope) }

    val menuBar = remember { MasterMenuBar(closeAct = onExit, viewModel = viewModel) }

    Window(
        title = transaction { StringLocale[ST_STR1_DM_WINDOW_NAME, act.name] },
        size = MASTER_WINDOW_SIZE,
        minimumSize = MASTER_WINDOW_SIZE,
        menuBar = menuBar,
        placement = WindowPlacement.Maximized
    ) {
        val playerDialogData = remember {
            PlayerDialog.PlayerDialogData(
                title = transaction { act.name },
                mapPanel = MapPanel(isParentMaster = false, viewModel = viewModel),
                onHide = { menuBar.togglePlayerFrameMenuItem.isSelected = false },
                getMasterWindowScreen = { window.graphicsConfiguration.device }
            )
        }

        DisposableEffect(Unit) {
            if (Settings.playerFrameOpenedByDefault) {
                playerDialogData.togglePlayerWindow(true)
            }

            onDispose {
                playerDialogData.togglePlayerWindow(false)
            }
        }

        LaunchedEffect(Unit) {
            menuBar.togglePlayerWindow = playerDialogData::togglePlayerWindow

            window.addKeyPressedListener {
                when (it.keyCode) {
                    KeyEvent.VK_UP -> viewModel.select()
                    KeyEvent.VK_DOWN -> viewModel.select(false)
                    KeyEvent.VK_RIGHT -> viewModel.rotateRight()
                    KeyEvent.VK_LEFT -> viewModel.rotateLeft()
                }
            }
        }

        MainContent(viewModel = viewModel)
    }
}

private fun PlayerDialog.PlayerDialogData.togglePlayerWindow(isVisible: Boolean) = PlayerDialog.toggle(this, isVisible)

@Composable
private fun MainContent(viewModel: MasterViewModel) = Row {
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