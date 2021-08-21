package jdr.exia.view.menubar

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuBarScope
import androidx.compose.ui.window.rememberTrayState
import jdr.exia.localization.*
import jdr.exia.model.act.Scene
import jdr.exia.model.command.CommandManager
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.tools.forElse
import jdr.exia.model.tools.withSetter
import jdr.exia.view.tools.*
import jdr.exia.view.ui.CTRL
import jdr.exia.view.ui.CTRLSHIFT
import jdr.exia.viewModel.MasterViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.event.ItemEvent
import java.awt.event.KeyEvent
import javax.swing.*

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
        setPlayerFrameOpenedByDefault = setPlayerFrameOpenedByDefault
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MenuBarScope.ToolsMenu(viewModel: MasterViewModel) = Menu(text = StringLocale[STR_TOOLS], mnemonic = 't') {
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
fun MenuBarScope.WindowMenu(
    closeAct: DefaultFunction,
    playerFrameOpenedByDefault: Boolean,
    setPlayerFrameOpenedByDefault: (Boolean) -> Unit,
) =
    Menu(text = StringLocale[STR_WINDOW], mnemonic = 'w') {
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
            val scenes = rememberTrayState()
        }
    }

/**
 * This is the DM Window menu bar (situated at the top)
 */
class MasterMenuBar(closeAct: DefaultFunction, private val viewModel: MasterViewModel) : JMenuBar() {
    private val undoMenuItem: JMenuItem

    private val redoMenuItem: JMenuItem

    val togglePlayerFrameMenuItem: JCheckBoxMenuItem

    lateinit var togglePlayerWindow: (Boolean) -> Unit

    private val windowParent
        get() = this.windowAncestor

    init {
        this.add(FileMenu())

        JMenu(StringLocale[STR_TOOLS]).applyAndAddTo(this) {
            JCheckBoxMenuItem(StringLocale[STR_ENABLE_CURSOR]).applyAndAddTo(this) {
                this.isSelected = Settings.cursorEnabled
                this.addItemListener {
                    Settings.cursorEnabled = it.stateChange == ItemEvent.SELECTED
                }
            }

            this.addSeparator()

            undoMenuItem = object : JMenuItem() {
                private val baseText = StringLocale[STR_CANCEL]

                override fun setText(text: String) =
                    super.setText("$baseText ${if (text == "") "" else "($text)"}")

                init {
                    this.isEnabled = false
                    this.addActionListener {
                        CommandManager(transaction { viewModel.act.currentScene.id }).undo()
                        viewModel.repaint()
                    }
                    this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, CTRL)
                }
            }

            this.add(undoMenuItem)

            redoMenuItem = object : JMenuItem() {
                override fun setText(text: String) =
                    super.setText("${StringLocale[STR_RESTORE]} ${if (text == "") "" else "($text)"}")

                init {
                    this.isEnabled = false
                    this.addActionListener {
                        CommandManager(transaction { viewModel.act.currentScene.id }).redo()
                        viewModel.repaint()
                    }
                    this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Y, CTRL)
                }
            }

            this.add(redoMenuItem)
        }

        JMenu(StringLocale[STR_WINDOW]).applyAndAddTo(this) {
            JMenuItem(StringLocale[STR_CLOSE_ACT]).applyAndAddTo(this) {
                this.addActionListener { closeAct() }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Q, CTRL)
            }

            this.addSeparator()

            togglePlayerFrameMenuItem = JCheckBoxMenuItem(StringLocale[STR_TOGGLE_PLAYER_FRAME]).applyAndAddTo(this) {
                this.isSelected = Settings.playerFrameOpenedByDefault

                this.addActionListener { e ->
                    togglePlayerWindow((e.source as AbstractButton).isSelected)

                    if (screens.size > 1)
                        viewModel.scope.launch {
                            delay(150)
                            windowParent?.requestFocus()
                        }

                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, CTRL)
            }

            this.addSeparator()

            JMenu(StringLocale[STR_CHOOSE_SCENE]).applyAndAddTo(this) {
                fun refreshSceneMenuItems() {
                    removeAll()

                    transaction {
                        viewModel.act.scenes.forEachIndexed { index, scene ->
                            if (scene.id.value == viewModel.act.currentScene.id.value) {
                                val item =
                                    JMenuItem("${index + 1} ${scene.name} (${StringLocale[STR_IS_CURRENT_SCENE, StringStates.NORMAL]})").apply {
                                        isEnabled = false
                                    }
                                add(item)
                            } else {
                                val item = JMenuItem("${index + 1} ${scene.name}")
                                item.addActionListener {
                                    viewModel.switchScene(scene)
                                    refreshSceneMenuItems()
                                }
                                add(item)
                            }
                        }
                    }
                }

                refreshSceneMenuItems()
            }
        }

        JMenu(StringLocale[STR_TOKENS]).applyAndAddTo(this) {
            JMenuItem(StringLocale[STR_MANAGE_BLUEPRINTS]).applyAndAddTo(this) {
                addActionListener { viewModel.showBlueprintEditor() }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_B, CTRLSHIFT)
            }

            JMenu(StringLocale[STR_IMPORT_FROM_SCENE]).applyAndAddTo(this) {
                transaction {
                    if (viewModel.act.scenes.count() <= 1)
                        isEnabled = false

                    viewModel.act.scenes.forEach {
                        if (it.id.value != viewModel.act.currentScene.id.value) {
                            val itemMenu = JMenu(it.name).apply {
                                if (it.elements.isNotEmpty()) {
                                    JMenuItem(StringLocale[STR_IMPORT_ALL_ELEMENTS]).applyAndAddTo(this) {
                                        addActionListener { _ ->
                                            it.elements.forEach { token ->
                                                transaction {
                                                    Scene.moveElementToScene(
                                                        token,
                                                        Scene[viewModel.act.currentScene.id.value]
                                                    )
                                                }
                                            }
                                            viewModel.repaint()
                                        }
                                    }

                                    this.add(JSeparator())
                                }

                                it.elements.forElse { token ->
                                    JMenuItem(token.name + " (" + token.type.localizedName + ")").applyAndAddTo(this) {
                                        addActionListener {
                                            transaction {
                                                Scene.moveElementToScene(
                                                    token,
                                                    Scene[viewModel.act.currentScene.id.value]
                                                )
                                            }
                                            viewModel.repaint()
                                        }
                                    }
                                } ?: run {
                                    isEnabled = false
                                }
                            }

                            add(itemMenu)
                        }
                    }

                    if (menuComponents.none { it is JMenuItem && it.isEnabled })
                        isEnabled = false
                }
            }

            this.addSeparator()

            JMenuItem(StringLocale[STR_DELETE_SELECTED_TOKENS]).applyAndAddTo(this) {
                this.addActionListener {
                    viewModel.removeElements()
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)
            }

            JMenuItem(StringLocale[STR_CLEAR_BOARD]).applyAndAddTo(this) {
                addActionListener {
                    showConfirmMessage(
                        this,
                        StringLocale[ST_CONFIRM_CLEAR_BOARD],
                        StringLocale[STR_DELETION],
                        confirm = true
                    ) {
                        transaction {
                            for (token in Scene[viewModel.act.currentScene.id.value].elements) {
                                viewModel.removeElements(listOf(token))
                                transaction { token.delete() }
                                Thread.sleep(100)
                            }
                        }
                    }
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, CTRLSHIFT)
            }
        }

        reloadCommandItemLabel()
    }

    private fun reloadCommandItemLabel(): Unit =
        CommandManager(transaction { viewModel.act.currentScene.id }).let { manager ->
            undoMenuItem.apply {
                isEnabled = manager.undoLabel != null
                text = manager.undoLabel ?: ""
            }

            redoMenuItem.apply {
                isEnabled = manager.redoLabel != null
                text = manager.redoLabel ?: ""
            }
        }
}