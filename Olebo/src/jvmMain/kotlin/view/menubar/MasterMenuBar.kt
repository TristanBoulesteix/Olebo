package jdr.exia.view.menubar

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuBarScope
import androidx.compose.ui.window.MenuScope
import jdr.exia.localization.*
import jdr.exia.model.act.Scene
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Element
import jdr.exia.model.tools.settableMutableStateOf
import jdr.exia.viewModel.MasterViewModel
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun FrameWindowScope.MasterMenuBar(
    exitApplication: () -> Unit,
    closeAct: () -> Unit,
    playerFrameOpenedByDefault: Boolean,
    setPlayerFrameOpenedByDefault: (Boolean) -> Unit,
    viewModel: MasterViewModel
) = MenuBar {
    MainMenus(exitApplication = exitApplication)
    ToolsMenu(viewModel = viewModel)
    WindowMenu(
        closeAct = closeAct,
        playerFrameOpenedByDefault = playerFrameOpenedByDefault,
        setPlayerFrameVisible = setPlayerFrameOpenedByDefault,
        currentScene = viewModel.currentScene,
        scenes = transaction { viewModel.act.scenes.toList() },
        onSwitchScene = viewModel::switchScene
    )
    TokenMenu(
        showBlueprintEditorDialog = viewModel::showBlueprintEditor,
        scenes = transaction { viewModel.act.scenes.toList() },
        currentScene = viewModel.currentScene,
        deleteSelectedToken = viewModel::removeElements,
        onClearTokens = { viewModel.confirmClearElement = true },
        moveElements = viewModel::moveElementsFromScene,
        selectNext = viewModel::select,
        selectPrevious = { viewModel.select(false) },
        selectAll = viewModel::selectAllElements,
        rotateRight = viewModel::rotateRight,
        rotateLeft = viewModel::rotateLeft,
        hasItemsSelected = viewModel.hasSelectedElement
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MenuBarScope.ToolsMenu(viewModel: MasterViewModel) = Menu(text = StringLocale[STR_TOOLS], mnemonic = 't') {
    var cursorEnabled by remember { settableMutableStateOf(Settings.cursorEnabled) { Settings.cursorEnabled = it } }

    CheckboxItem(
        text = StringLocale[STR_ENABLE_CURSOR],
        checked = cursorEnabled,
        onCheckedChange = { cursorEnabled = it },
        shortcut = KeyShortcut(Key.C, shift = true)
    )

    Separator()

    Item(
        text = StringLocale[STR_CANCEL] + if (viewModel.commandManager.undoLabel.isNullOrBlank()) "" else " (${viewModel.commandManager.undoLabel})",
        shortcut = KeyShortcut(Key.Z, ctrl = true),
        enabled = viewModel.commandManager.hasUndoAction
    ) {
        viewModel.commandManager.undo()
        viewModel.refreshView()
    }

    Item(
        text = StringLocale[STR_RESTORE] + if (viewModel.commandManager.redoLabel.isNullOrBlank()) "" else " (${viewModel.commandManager.redoLabel})",
        shortcut = KeyShortcut(Key.Y, ctrl = true),
        enabled = viewModel.commandManager.hasRedoAction
    ) {
        viewModel.commandManager.redo()
        viewModel.refreshView()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MenuBarScope.WindowMenu(
    closeAct: () -> Unit,
    playerFrameOpenedByDefault: Boolean,
    setPlayerFrameVisible: (Boolean) -> Unit,
    currentScene: Scene,
    scenes: List<Scene>,
    onSwitchScene: (Scene) -> Unit
) = Menu(text = StringLocale[STR_WINDOW], mnemonic = 'w') {
    Item(text = StringLocale[STR_CLOSE_ACT], shortcut = KeyShortcut(key = Key.Q, ctrl = true), onClick = closeAct)

    Separator()

    CheckboxItem(
        text = StringLocale[STR_TOGGLE_PLAYER_FRAME],
        shortcut = KeyShortcut(Key.O, ctrl = true),
        checked = playerFrameOpenedByDefault,
        onCheckedChange = setPlayerFrameVisible
    )

    Separator()

    Menu(text = StringLocale[STR_CHOOSE_SCENE]) {
        scenes.forEachIndexed { index, scene ->
            val isCurrentScene = remember(currentScene) { scene.id == currentScene.id }
            val itemText = remember(isCurrentScene) {
                "${index + 1} ${scene.name}" + if (isCurrentScene) " (${StringLocale[STR_IS_CURRENT_SCENE, StringStates.NORMAL]})" else ""
            }

            Item(text = itemText, enabled = !isCurrentScene) {
                onSwitchScene(scene)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MenuBarScope.TokenMenu(
    showBlueprintEditorDialog: () -> Unit,
    scenes: List<Scene>,
    currentScene: Scene,
    deleteSelectedToken: () -> Unit,
    onClearTokens: () -> Unit,
    moveElements: (List<Element>) -> Unit,
    rotateRight: () -> Unit,
    rotateLeft: () -> Unit,
    selectAll: () -> Unit,
    selectNext: () -> Unit,
    selectPrevious: () -> Unit,
    hasItemsSelected: Boolean
) = Menu(text = StringLocale[STR_TOKENS], mnemonic = 'b') {
    Item(
        text = StringLocale[STR_MANAGE_BLUEPRINTS],
        shortcut = KeyShortcut(ctrl = true, shift = true, key = Key.B),
        onClick = showBlueprintEditorDialog
    )

    MenuImportFromScene(scenes = scenes, currentScene = currentScene, moveElements = moveElements)

    Separator()

    Item(
        text = StringLocale[STR_ROTATE_TO_RIGHT],
        shortcut = KeyShortcut(key = Key.DirectionRight),
        onClick = rotateRight,
        enabled = hasItemsSelected
    )

    Item(
        text = StringLocale[STR_ROTATE_TO_LEFT],
        shortcut = KeyShortcut(key = Key.DirectionLeft),
        onClick = rotateLeft,
        enabled = hasItemsSelected
    )

    Separator()

    Item(
        text = StringLocale[STR_SELECT_DOWN],
        shortcut = KeyShortcut(key = Key.DirectionDown),
        onClick = selectPrevious
    )

    Item(
        text = StringLocale[STR_SELECT_UP],
        shortcut = KeyShortcut(key = Key.DirectionUp),
        onClick = selectNext
    )

    Item(
        text = StringLocale[STR_SELECT_ALL],
        shortcut = KeyShortcut(key = Key.A, ctrl = true),
        onClick = selectAll
    )

    Separator()

    Item(
        text = StringLocale[STR_DELETE_SELECTED_TOKENS],
        shortcut = KeyShortcut(key = Key.Delete),
        onClick = deleteSelectedToken
    )

    Item(
        text = StringLocale[STR_CLEAR_BOARD],
        shortcut = KeyShortcut(ctrl = true, shift = true, key = Key.Delete),
        onClick = onClearTokens
    )
}

@Composable
private fun MenuScope.MenuImportFromScene(
    scenes: List<Scene>,
    currentScene: Scene,
    moveElements: (List<Element>) -> Unit
) = Menu(text = StringLocale[STR_IMPORT_FROM_SCENE], enabled = scenes.count() > 1) {
    scenes.forEach {
        /*
        Since moving elements needs to trigger recomposition, we create a mutableStateList from the list of elements.
        This list only needs to be refreshed when current scene is changing.
         */
        val elements = remember(currentScene, it.elements) { it.elements.toMutableStateList() }

        if (it.id != currentScene.id) {
            Menu(text = it.name, enabled = elements.isNotEmpty()) {
                if (elements.isNotEmpty()) {
                    Item(text = StringLocale[STR_IMPORT_ALL_ELEMENTS]) {
                        elements.moveElementToSceneAndUpdateState(
                            elements = it.elements,
                            moveElements = moveElements
                        )
                    }

                    Separator()

                    elements.forEach { token ->
                        Item(text = "${token.name} (${token.type.localizedName})") {
                            elements.moveElementToSceneAndUpdateState(
                                elements = listOf(token),
                                moveElements = moveElements
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun SnapshotStateList<Element>.moveElementToSceneAndUpdateState(
    elements: List<Element>,
    moveElements: (List<Element>) -> Unit
) {
    moveElements(elements)
    this.removeAll(elements.toSet())
}