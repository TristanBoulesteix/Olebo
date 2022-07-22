package jdr.exia.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPlacement
import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.command.CommandManager
import jdr.exia.model.dao.option.Settings
import jdr.exia.view.composable.master.*
import jdr.exia.view.element.dialog.ConfirmMessage
import jdr.exia.view.menubar.MasterMenuBar
import jdr.exia.view.tools.event.addMousePressedListener
import jdr.exia.view.tools.event.removeMousePressedListener
import jdr.exia.view.tools.screens
import jdr.exia.view.ui.MASTER_WINDOW_SIZE
import jdr.exia.viewModel.MasterViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Frame.MAXIMIZED_BOTH
import java.awt.GraphicsConfiguration
import java.awt.GraphicsDevice
import java.awt.Rectangle
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun ApplicationScope.MasterWindow(act: Act, onExit: () -> Unit) {
    val scope = rememberCoroutineScope()

    val viewModel = remember { MasterViewModel(act, scope) }

    Window(
        title = transaction { StringLocale[ST_STR1_DM_WINDOW_NAME, act.name] },
        size = MASTER_WINDOW_SIZE,
        minimumSize = MASTER_WINDOW_SIZE,
        placement = WindowPlacement.Maximized
    ) {
        LaunchedEffect(Unit) {
            window.extendedState = MAXIMIZED_BOTH

            addSettingsChangedListener {
                viewModel.repaint(reloadTokens = true)
            }
        }

        var playerFrameVisible by remember { mutableStateOf(Settings.playerFrameOpenedByDefault) }

        val playerDialogData = remember {
            PlayerDialog.PlayerDialogData(
                title = transaction { act.name },
                mapPanel = MapPanel(isParentMaster = false, viewModel = viewModel),
                onHide = { playerFrameVisible = false },
                getMasterWindowScreen = window::getCurrentScreen
            )
        }

        LaunchedEffect(playerFrameVisible) {
            playerDialogData.togglePlayerWindow(playerFrameVisible)

            if (screens.size > 1)
                launch(context = EmptyCoroutineContext) {
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
                CommandManager.clear()
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
                onConfirm = { viewModel.removeElements(viewModel.elements) }
            )
        }
    }
}

private fun PlayerDialog.PlayerDialogData.togglePlayerWindow(isVisible: Boolean) = PlayerDialog.toggle(this, isVisible)

@Composable
private fun MainContent(viewModel: MasterViewModel) = Row {
    Items(viewModel)

    Column(modifier = Modifier.weight(.80f).fillMaxSize()) {
        val focusManager = LocalFocusManager.current

        DisposableEffect(focusManager) {
            val panel = viewModel.panel

            val event = panel.addMousePressedListener { focusManager.clearFocus() }

            onDispose {
                panel.removeMousePressedListener(event)
            }
        }

        ComposeMapPanel(modifier = Modifier.weight(.85f).fillMaxSize(), viewModel = viewModel)
        //SwingPanel(factory = viewModel::panel, modifier = Modifier.weight(.80f).fillMaxSize())

        BottomPanel(
            modifier = Modifier.weight(.20f).fillMaxSize(),
            selectedEditor = {
                SelectedEditor(
                    commandManager = viewModel.commandManager,
                    selectedElements = viewModel.selectedElements,
                    repaint = viewModel::repaint,
                    deleteSelectedElement = viewModel::removeElements,
                    setPriority = viewModel::changePriority
                )
            },
            shareScene = {
                ShareScenePanel(
                    connect = viewModel::connectToServer,
                    connectionState = viewModel.connectionState,
                    disconnect = viewModel::disconnectFromServer
                )
            }
        )
    }
}

/**
 * The lateral list of items. It is on a separated function to prevent blink with Swing ComboBox on recomposition
 */
@Composable
private fun RowScope.Items(viewModel: MasterViewModel) {
    ItemList(
        modifier = Modifier.Companion.weight(.20f),
        createElement = viewModel::addNewElement,
        items = viewModel.itemsFiltered,
        searchString = viewModel.searchString,
        onSearch = { viewModel.searchString = it }
    )
}

/**
 * Function to find current screen of the window.
 * The function [GraphicsConfiguration.getDevice] is not enough since it returns only the screen where the window was opened.
 *
 * @return The current [GraphicsDevice] of the [Window] or null if it was unable to get the current screen
 */
private fun ComposeWindow.getCurrentScreen(): GraphicsDevice? {
    val windowBounds: Rectangle = bounds

    var lastArea = 0
    var device: GraphicsDevice? = null

    screens.forEach { graphicsDevice ->
        graphicsDevice.configurations.forEach { graphicsConfiguration: GraphicsConfiguration ->
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