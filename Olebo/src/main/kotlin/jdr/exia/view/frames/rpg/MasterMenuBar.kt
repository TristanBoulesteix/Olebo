package jdr.exia.view.frames.rpg

import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.command.CommandManager
import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.utils.toElements
import jdr.exia.utils.forElse
import jdr.exia.view.frames.editor.elements.BlueprintDialog
import jdr.exia.view.frames.home.HomeFrame
import jdr.exia.view.utils.CTRL
import jdr.exia.view.utils.CTRLSHIFT
import jdr.exia.view.utils.applyAndAppendTo
import jdr.exia.view.utils.components.FileMenu
import jdr.exia.view.utils.showConfirmMessage
import jdr.exia.viewModel.ViewManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.event.ItemEvent
import java.awt.event.KeyEvent
import javax.swing.*

/**
 * This is MasterFrame's menu bar (situated at the top)
 */
object MasterMenuBar : JMenuBar() {
    private var undoMenuItem: JMenuItem? = null

    private var redoMenuItem: JMenuItem? = null

    var act: Act? = null

    var togglePlayerFrameMenuItem: JCheckBoxMenuItem? = null
        private set

    fun initialize() {
        this.removeAll()

        this.add(FileMenu())

        JMenu(Strings[STR_TOOLS]).applyAndAppendTo(this) {
            JCheckBoxMenuItem(Strings[STR_ENABLE_CURSOR]).applyAndAppendTo(this) {
                this.isSelected = Settings.cursorEnabled
                this.addItemListener {
                    Settings.cursorEnabled = it.stateChange == ItemEvent.SELECTED
                }
            }

            this.addSeparator()

            undoMenuItem = object : JMenuItem() {
                private val baseText = Strings[STR_CANCEL]

                override fun setText(text: String) =
                    super.setText("$baseText ${if (text == "") "" else "($text)"}")

                init {
                    this.isEnabled = false
                    this.addActionListener {
                        act?.let {
                            CommandManager(it.sceneId).undo()
                            ViewManager.repaint()
                        }
                    }
                    this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, CTRL)
                }
            }

            this.add(undoMenuItem)

            redoMenuItem = object : JMenuItem() {
                override fun setText(text: String) =
                    super.setText("${Strings[STR_RESTORE]} ${if (text == "") "" else "($text)"}")

                init {
                    this.isEnabled = false
                    this.addActionListener {
                        act?.let {
                            CommandManager(it.sceneId).redo()
                            ViewManager.repaint()
                        }
                    }
                    this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Y, CTRL)
                }
            }

            this.add(redoMenuItem)
        }

        JMenu(Strings[STR_WINDOW]).applyAndAppendTo(this) {
            JMenuItem(Strings[STR_CLOSE_ACT]).applyAndAppendTo(this) {
                this.addActionListener {
                    MasterFrame.isVisible = false
                    PlayerFrame.hide()
                    HomeFrame().isVisible = true
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Q, CTRL)
            }

            this.addSeparator()

            togglePlayerFrameMenuItem = JCheckBoxMenuItem(Strings[STR_TOGGLE_PLAYER_FRAME]).applyAndAppendTo(this) {
                this.isSelected = Settings.playerFrameOpenedByDefault
                this.addActionListener { e ->
                    PlayerFrame.toggle((e.source as AbstractButton).isSelected)
                    MasterFrame.requestFocus()
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, CTRL)
            }

            this.addSeparator()

            JMenu(Strings[STR_CHOOSE_SCENE]).applyAndAppendTo(this) {
                act?.let {
                    it.scenes.forEachIndexed { index, scene ->
                        if (scene.id.value == it.sceneId) {
                            val item =
                                JMenuItem("${index + 1} ${scene.name} (${Strings[STR_IS_CURRENT_SCENE, StringStates.NORMAL]})").apply {
                                    isEnabled = false
                                }
                            this.add(item)
                        } else {
                            val item = JMenuItem("${index + 1} ${scene.name}")
                            item.addActionListener { transaction { ViewManager.changeCurrentScene(scene.id.value) } }
                            this.add(item)
                        }
                    }
                }
            }
        }

        JMenu(Strings[STR_TOKENS]).applyAndAppendTo(this) {
            JMenuItem(Strings[STR_MANAGE_BLUEPRINTS]).applyAndAppendTo(this) {
                addActionListener {
                    BlueprintDialog().isVisible = true
                    ViewManager.unselectAllElements()
                    ViewManager.repaint()
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_B, CTRLSHIFT)
            }

            JMenu(Strings[STR_IMPORT_FROM_SCENE]).applyAndAppendTo(this) {
                act?.let { act ->
                    if (act.scenes.size <= 1)
                        this.isEnabled = false

                    act.scenes.forEach {
                        if (it.id.value != act.sceneId) {
                            val itemMenu = JMenu(it.name).apply {
                                if (it.elements.isNotEmpty()) {
                                    JMenuItem(Strings[STR_IMPORT_ALL_ELEMENTS]).applyAndAppendTo(this) {
                                        addActionListener { _ ->
                                            it.elements.forEach { token ->
                                                transaction {
                                                    Scene.moveElementToScene(
                                                        token,
                                                        Scene[act.sceneId]
                                                    )
                                                }
                                            }
                                            ViewManager.repaint()
                                            initialize()
                                            reloadCommandItemLabel()
                                        }
                                    }

                                    this.add(JSeparator())
                                }

                                it.elements.forElse { token ->
                                    JMenuItem(token.name + " (" + token.type.name + ")").applyAndAppendTo(this) {
                                        addActionListener {
                                            transaction {
                                                Scene.moveElementToScene(
                                                    token,
                                                    Scene[act.sceneId]
                                                )
                                            }
                                            ViewManager.repaint()
                                        }
                                    }
                                } ?: run {
                                    isEnabled = false
                                }
                            }

                            this.add(itemMenu)
                        }
                    }

                    if (this.menuComponents.none { it is JMenuItem && it.isEnabled })
                        this.isEnabled = false
                }
            }

            this.addSeparator()

            JMenuItem(Strings[STR_DELETE_SELECTED_TOKENS]).applyAndAppendTo(this) {
                this.addActionListener {
                    ViewManager.removeSelectedElements()
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)
            }

            JMenuItem(Strings[STR_CLEAR_BOARD]).applyAndAppendTo(this) {
                addActionListener {
                    showConfirmMessage(this, Strings[ST_CONFIRM_CLEAR_BOARD], Strings[STR_DELETION]) {
                        transaction {
                            for (token in Scene[act!!.sceneId].elements) {
                                ViewManager.removeElements(token.toElements())
                                transaction { token.delete() }
                                Thread.sleep(100)
                            }
                        }
                    }
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, CTRLSHIFT)
            }
        }
    }

    fun reloadCommandItemLabel() = CommandManager(act?.sceneId ?: -1).let { manager ->
        undoMenuItem?.apply {
            isEnabled = manager.undoLabel != null
            text = manager.undoLabel ?: ""
        }

        redoMenuItem?.apply {
            isEnabled = manager.redoLabel != null
            text = manager.redoLabel ?: ""
        }
    }
}