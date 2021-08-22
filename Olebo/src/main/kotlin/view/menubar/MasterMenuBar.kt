package jdr.exia.view.menubar

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuBarScope
import jdr.exia.localization.*
import jdr.exia.model.act.Scene
import jdr.exia.model.command.CommandManager
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Element
import jdr.exia.model.tools.withSetter
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.viewModel.MasterViewModel
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun FrameWindowScope.MasterMenuBar(
    exitApplication: DefaultFunction,
    closeAct: DefaultFunction,
    playerFrameOpenedByDefault: Boolean,
    setPlayerFrameOpenedByDefault: (Boolean) -> Unit,
    viewModel: MasterViewModel
) = MenuBar {
    MainMenus(exitApplication = exitApplication)
    ToolsMenu(viewModel = viewModel)
    WindowMenu(
        closeAct = closeAct,
        playerFrameOpenedByDefault = playerFrameOpenedByDefault,
        setPlayerFrameOpenedByDefault = setPlayerFrameOpenedByDefault,
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
        repaint = viewModel::repaint
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MenuBarScope.ToolsMenu(viewModel: MasterViewModel) = Menu(text = StringLocale[STR_TOOLS], mnemonic = 't') {
    var cursorEnabled by remember { mutableStateOf(Settings.cursorEnabled) withSetter { Settings.cursorEnabled = it } }

    CheckboxItem(
        text = StringLocale[STR_ENABLE_CURSOR],
        checked = cursorEnabled,
        onCheckedChange = { cursorEnabled = it }
    )

    Separator()

    val commandManager by remember { mutableStateOf(CommandManager(transaction { viewModel.act.currentScene.id })) }

    Item(
        text = StringLocale[STR_CANCEL] + if (commandManager.undoLabel.isNullOrBlank()) "" else " (${commandManager.undoLabel})",
        shortcut = KeyShortcut(Key.Z, ctrl = true),
        enabled = commandManager.hasUndoAction
    ) {
        CommandManager(transaction { viewModel.act.currentScene.id }).undo()
        viewModel.repaint()
    }

    Item(
        text = StringLocale[STR_RESTORE] + if (commandManager.redoLabel.isNullOrBlank()) "" else " (${commandManager.redoLabel})",
        shortcut = KeyShortcut(Key.Y, ctrl = true),
        enabled = commandManager.hasRedoAction
    ) {
        CommandManager(transaction { viewModel.act.currentScene.id }).redo()
        viewModel.repaint()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MenuBarScope.WindowMenu(
    closeAct: DefaultFunction,
    playerFrameOpenedByDefault: Boolean,
    setPlayerFrameOpenedByDefault: (Boolean) -> Unit,
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
        onCheckedChange = setPlayerFrameOpenedByDefault
    )

    Separator()

    Menu(text = StringLocale[STR_CHOOSE_SCENE]) {
        scenes.forEachIndexed { index, scene ->
            val isCurrentScene by remember(currentScene) { derivedStateOf { scene.id == currentScene.id } }
            val itemText by remember(isCurrentScene) { derivedStateOf { "${index + 1} ${scene.name}" + if (isCurrentScene) " (${StringLocale[STR_IS_CURRENT_SCENE, StringStates.NORMAL]})" else "" } }

            Item(text = itemText, enabled = !isCurrentScene) {
                onSwitchScene(scene)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MenuBarScope.TokenMenu(
    showBlueprintEditorDialog: DefaultFunction,
    scenes: List<Scene>,
    currentScene: Scene,
    deleteSelectedToken: DefaultFunction,
    onClearTokens: DefaultFunction,
    repaint: DefaultFunction
) = Menu(text = StringLocale[STR_TOKENS], mnemonic = 'b') {
    Item(
        text = StringLocale[STR_MANAGE_BLUEPRINTS],
        shortcut = KeyShortcut(ctrl = true, shift = true, key = Key.B),
        onClick = showBlueprintEditorDialog
    )

    MenuImportFromScene(scenes = scenes, currentScene = currentScene, repaint = repaint)

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
private fun MenuBarScope.MenuImportFromScene(scenes: List<Scene>, currentScene: Scene, repaint: DefaultFunction) =
    Menu(text = StringLocale[STR_IMPORT_FROM_SCENE], enabled = scenes.count() > 1) {
        scenes.forEach {
            /*
            Since moving elements needs to trigger recomposition, we create a mutableStateList from the list of elements.
            This list only needs to be refreshed when current scene is changing.
             */
            val elements = remember(currentScene) { it.elements.toMutableStateList() }

            if (it.id != currentScene.id) {
                Menu(text = it.name, enabled = elements.isNotEmpty()) {
                    if (elements.isNotEmpty()) {
                        Item(text = StringLocale[STR_IMPORT_ALL_ELEMENTS]) {
                            elements.moveElementToSceneAndUpdateState(currentScene, it.elements)
                            repaint()
                        }

                        Separator()

                        elements.forEach { token ->
                            Item(text = "${token.name} (${token.type.localizedName})") {
                                elements.moveElementToSceneAndUpdateState(currentScene, listOf(token))
                                repaint()
                            }
                        }
                    }
                }
            }
        }
    }

private fun SnapshotStateList<Element>.moveElementToSceneAndUpdateState(scene: Scene, elements: List<Element>) {
    Scene.moveElementToScene(scene, elements)
    this.removeAll(elements)
}