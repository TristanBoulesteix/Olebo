package view.rpgFrames

import model.act.Act
import model.act.Scene
import model.dao.DAO
import view.editor.elements.BlueprintDialog
import view.homeFrame.HomeFrame
import view.utils.*
import view.utils.components.FileMenu
import viewModel.ViewManager
import org.jetbrains.exposed.sql.transactions.transaction
import view.utils.*
import java.awt.event.KeyEvent
import javax.swing.*

/**
 * This is MasterFrame's menu bar (situated at the top)
 */
object MasterMenuBar : JMenuBar() {
    var act: Act? = null

    var togglePlayerFrameMenuItem: JCheckBoxMenuItem? = null
        private set

    fun initialize() {
        this.removeAll()

        this.add(FileMenu())

        JMenu("Fenêtres").applyAndAppendTo(this) {
            JMenuItem("Fermer scenario").applyAndAppendTo(this) {
                this.addActionListener {
                    MasterFrame.isVisible = false
                    PlayerFrame.hide()
                    HomeFrame().isVisible = true
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Q, CTRL)
            }

            this.addSeparator()

            togglePlayerFrameMenuItem = JCheckBoxMenuItem("Fenetre PJs ON/OFF").applyAndAppendTo(this) {
                this.addActionListener { e ->
                    (e.source as AbstractButton).let {
                        if (it.isSelected)
                            PlayerFrame.show()
                        else PlayerFrame.hide()
                    }
                }
            }

            this.addSeparator()

            JMenu("Choisir une Scene").applyAndAppendTo(this) {
                if (act != null) {
                    var i = 0

                    for (scene in act!!.scenes) { //Pour chaque scene, on créé une option pour activer la scene
                        i++
                        if (scene.id.value == act!!.sceneId) {
                            val item = JMenuItem("$i ${scene.name} (Active)")
                            this.add(item)
                        } else {
                            val item = JMenuItem("$i ${scene.name}")
                            item.addActionListener { transaction(DAO.database) { ViewManager.changeCurrentScene(scene.id.value) } }
                            this.add(item)
                        }
                    }
                }
            }
        }

        JMenu("Pions").applyAndAppendTo(this) {
            JMenuItem("Gèrer les Blueprints").applyAndAppendTo(this) {
                addActionListener {
                    BlueprintDialog().isVisible = true
                    MasterFrame.itemPanel.reloadContent()
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_B, CTRLSHIFT)
            }

            JMenu("Importer depuis une autre scene").applyAndAppendTo(this) {
                act?.let { act ->
                    if (act.scenes.size <= 1)
                        this.isEnabled = false

                    act.scenes.forEach {
                        if (it.id.value != act.sceneId) {
                            val itemMenu = JMenu(it.name).apply {
                                it.elements.forElse { token ->
                                    val item = JMenuItem(token.name + " (" + token.type.name + ")").apply {
                                        addActionListener {
                                            transaction(DAO.database) { Scene.moveElementToScene(token, Scene[act.sceneId]) }
                                            ViewManager.repaint()
                                        }
                                    }

                                    this.add(item)
                                } ?: { this.isEnabled = false }()
                            }

                            this.add(itemMenu)
                        }
                    }

                    if (this.menuComponents.none { it is JMenuItem && it.isEnabled })
                        this.isEnabled = false
                }
            }

            this.addSeparator()

            JMenuItem("Supprimer pion(s) selectionné(s)").applyAndAppendTo(this) {
                this.addActionListener {
                    SelectPanel.selectedElements.forEach(ViewManager::removeToken)
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)
            }

            JMenuItem("Vider le plateau").applyAndAppendTo(this) {
                addActionListener {
                    showConfirmMessage(this, "Voulez-vous vraiment supprimer tous les éléments du plateau ? Cette action est irréversible.", "Suppression") {
                        transaction(DAO.database) {
                            for (token in Scene[act!!.sceneId].elements) {
                                ViewManager.removeToken(token)
                                transaction(DAO.database) { token.delete() }
                                Thread.sleep(100)
                            }
                        }
                    }
                }
                this.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, CTRLSHIFT)
            }
        }
    }
}