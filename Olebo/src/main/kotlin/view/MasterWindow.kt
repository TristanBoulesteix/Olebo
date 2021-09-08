package jdr.exia.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPlacement
import jdr.exia.localization.STR_DELETION
import jdr.exia.localization.ST_CONFIRM_CLEAR_BOARD
import jdr.exia.localization.ST_STR1_DM_WINDOW_NAME
import jdr.exia.localization.StringLocale
import jdr.exia.model.act.Act
import jdr.exia.model.dao.option.Settings
import jdr.exia.view.composable.master.ItemList
import jdr.exia.view.composable.master.MapPanel
import jdr.exia.view.composable.master.SelectedEditor
import jdr.exia.view.element.dialog.ConfirmMessage
import jdr.exia.view.menubar.MasterMenuBar
import jdr.exia.view.tools.screens
import jdr.exia.view.ui.MASTER_WINDOW_SIZE
import jdr.exia.viewModel.MasterViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.GraphicsDevice

@Composable
fun ApplicationScope.MasterWindow(act: Act, onExit: () -> Unit) {
    val scope = rememberCoroutineScope()

    val viewModel = remember { MasterViewModel(act = act, scope = scope) }

    Window(
        title = transaction { StringLocale[ST_STR1_DM_WINDOW_NAME, act.name] },
        size = MASTER_WINDOW_SIZE,
        minimumSize = MASTER_WINDOW_SIZE,
        placement = WindowPlacement.Maximized
    ) {
        var playerFrameVisible by remember { mutableStateOf(Settings.playerFrameOpenedByDefault) }

        val playerDialogData = remember {
            PlayerDialog.PlayerDialogData(
                title = transaction { act.name },
                mapPanel = MapPanel(isParentMaster = false, viewModel = viewModel),
                onHide = { playerFrameVisible = false },
                getMasterWindowScreen = window::getCurrentSceen
            )
        }

        LaunchedEffect(playerFrameVisible) {
            playerDialogData.togglePlayerWindow(playerFrameVisible)

            if (screens.size > 1)
                launch {
                    delay(150)
                    window.requestFocus()
                }
        }

        MasterMenuBar(
            exitApplication = ::exitApplication,
            closeAct = onExit,
            playerFrameOpenedByDefault = playerFrameVisible,
            setPlayerFrameOpenedByDefault = { playerFrameVisible = it },
            viewModel = viewModel
        )

        DisposableEffect(Unit) {
            onDispose {
                playerDialogData.togglePlayerWindow(false)
            }
        }

        MainContent(viewModel = viewModel)

        if (viewModel.blueprintEditorDialogVisible) {
            BlueprintEditorDialog(onCloseRequest = viewModel::hideBlueprintEditor)
        }

        if (viewModel.confirmClearElement) {
            ConfirmMessage(
                message = StringLocale[ST_CONFIRM_CLEAR_BOARD],
                title = StringLocale[STR_DELETION],
                onCloseRequest = { viewModel.confirmClearElement = false },
                onConfirm = { viewModel.removeElements(viewModel.tokens) }
            )
        }
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
            deleteSelectedElement = viewModel::removeElements
        )
    }
}

/**
 * Function to find current screen of the window.
 * The function [GraphicsConfiguration::getDevice] is not enough since it returns only the screen where the window was opened.
 *
 * @return The current GraphicsDevice of the Window or null if it was unable to get the current screen
 */
private fun ComposeWindow.getCurrentSceen(): GraphicsDevice? {
    val windowBounds = bounds

    var lastArea = 0
    var device: GraphicsDevice? = null

    screens.forEach { graphicsDevice ->
        graphicsDevice.configurations.forEach { graphicsConfiguration ->
            val area = windowBounds.intersection(graphicsConfiguration.bounds).let { it.width * it.height }

            if (area != 0) {
                if (area > lastArea) {
                    lastArea = area
                    device = graphicsDevice
                }
            }
        }
    }

    return device
}