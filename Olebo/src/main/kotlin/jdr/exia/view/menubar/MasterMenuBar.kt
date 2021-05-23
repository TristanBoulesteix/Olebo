package jdr.exia.view.menubar

import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.command.CommandManager
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.tools.forElse
import jdr.exia.view.tools.applyAndAddTo
import jdr.exia.view.tools.screens
import jdr.exia.view.tools.showConfirmMessage
import jdr.exia.view.ui.CTRL
import jdr.exia.view.ui.CTRLSHIFT
import jdr.exia.viewModel.MainViewModel
import jdr.exia.viewModel.legacy.ViewManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.event.ItemEvent
import java.awt.event.KeyEvent
import javax.swing.*

/**
 * This is the DM Window menu bar (situated at the top)
 */
class MasterMenuBar(val act: Act, viewModel: MainViewModel) : JMenuBar() {
    private val undoMenuItem: JMenuItem

    private val redoMenuItem: JMenuItem

    val togglePlayerFrameMenuItem: JCheckBoxMenuItem

    init {
        this.removeAll()

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
                        CommandManager(transaction { act.currentScene.id }).undo()
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
                        CommandManager(transaction { act.currentScene.id }).redo()
                        viewModel.repaint()
                    }
                    this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Y, CTRL)
                }
            }

            this.add(redoMenuItem)
        }

        JMenu(StringLocale[STR_WINDOW]).applyAndAddTo(this) {
            JMenuItem(StringLocale[STR_CLOSE_ACT]).applyAndAddTo(this) {
                this.addActionListener { viewModel.closeAct() }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Q, CTRL)
            }

            this.addSeparator()

            togglePlayerFrameMenuItem = JCheckBoxMenuItem(StringLocale[STR_TOGGLE_PLAYER_FRAME]).applyAndAddTo(this) {
                this.isSelected = Settings.playerFrameOpenedByDefault

                this.addActionListener { e ->
                    viewModel.togglePlayerWindow((e.source as AbstractButton).isSelected)

                    if (screens.size > 1)
                        viewModel.focusMasterWindow()
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, CTRL)
            }

            this.addSeparator()

            JMenu(StringLocale[STR_CHOOSE_SCENE]).applyAndAddTo(this) {
                transaction {
                    act.scenes.forEachIndexed { index, scene ->
                        if (scene.id.value == act.currentScene.id.value) {
                            val item =
                                JMenuItem("${index + 1} ${scene.name} (${StringLocale[STR_IS_CURRENT_SCENE, StringStates.NORMAL]})").apply {
                                    isEnabled = false
                                }
                            add(item)
                        } else {
                            val item = JMenuItem("${index + 1} ${scene.name}")
                            item.addActionListener { transaction { ViewManager.changeCurrentScene(scene.id.value) } } // TODO
                            add(item)
                        }
                    }
                }
            }
        }

        JMenu(StringLocale[STR_TOKENS]).applyAndAddTo(this) {
            JMenuItem(StringLocale[STR_MANAGE_BLUEPRINTS]).applyAndAddTo(this) {
                addActionListener {
                    // TODO BlueprintsDialog(windowAncestor).isVisible = true
                    viewModel.unselectElements()
                    viewModel.repaint()
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_B, CTRLSHIFT)
            }

            JMenu(StringLocale[STR_IMPORT_FROM_SCENE]).applyAndAddTo(this) {
                transaction {
                    if (act.scenes.count() <= 1)
                        isEnabled = false

                    act.scenes.forEach {
                        if (it.id.value != act.currentScene.id.value) {
                            val itemMenu = JMenu(it.name).apply {
                                if (it.elements.isNotEmpty()) {
                                    JMenuItem(StringLocale[STR_IMPORT_ALL_ELEMENTS]).applyAndAddTo(this) {
                                        addActionListener { _ ->
                                            it.elements.forEach { token ->
                                                transaction {
                                                    Scene.moveElementToScene(
                                                        token,
                                                        Scene[act.currentScene.id.value]
                                                    )
                                                }
                                            }
                                            viewModel.repaint()
                                        }
                                    }

                                    this.add(JSeparator())
                                }

                                it.elements.forElse { token ->
                                    JMenuItem(token.name + " (" + token.type.name + ")").applyAndAddTo(this) {
                                        addActionListener {
                                            transaction {
                                                Scene.moveElementToScene(
                                                    token,
                                                    Scene[act.currentScene.id.value]
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
                            for (token in Scene[act.currentScene.id.value].elements) {
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

    fun reloadCommandItemLabel() = CommandManager(transaction { act.currentScene.id }).let { manager ->
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