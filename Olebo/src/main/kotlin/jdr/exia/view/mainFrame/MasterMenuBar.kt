package jdr.exia.view.mainFrame

import jdr.exia.controller.ViewManager
import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.dao.DAO
import jdr.exia.view.editor.elements.BlueprintDialog
import jdr.exia.view.homeFrame.HomeFrame
import org.jetbrains.exposed.sql.transactions.transaction
import javax.swing.*

/**
 * This is MasterFrame's menu bar (situated at the top)
 */
object MasterMenuBar : JMenuBar() {
    var act: Act? = null

    private fun <T: JComponent> T.applyAndAppend(parent : JComponent, block: T.() -> Unit) {
        this.apply(block)
        parent.add(this)
    }

    fun initialize() {
        this.removeAll()

        JMenu("Fenêtres").applyAndAppend(this) {
            JMenuItem("Fermer scenario").applyAndAppend(this) {
                this.addActionListener {
                    MasterFrame.isVisible = false;
                    PlayerFrame.isVisible = false;
                    HomeFrame().isVisible = true
                }
            }

            this.addSeparator()

            JMenuItem("Fenetre PJs ON/OFF").applyAndAppend(this) {
                this.addActionListener {
                    PlayerFrame.toggleDisplay()
                }
            }

            this.addSeparator()

            JMenu("Choisir une Scene").applyAndAppend(this) {
                if (act != null) {
                    var i = 0

                    for (scene in act!!.scenes) { //Pour chaque scene, on créé une option pour activer la scene
                        i++
                        if (scene.id.value == act!!.sceneId) {
                            val item = JMenuItem("$i ${scene.name} (Active)")
                            this.add(item)
                        } else {
                            val item = JMenuItem("$i ${scene.name}")
                            item.addActionListener { transaction(DAO.database) { ViewManager.changeCurrentScene(scene.id.value); } }
                            this.add(item)
                        }
                    }
                }
            }
        }

        JMenu("Pions").applyAndAppend(this) {
            JMenuItem("Gèrer les Blueprints").applyAndAppend(this) {
                addActionListener {
                    BlueprintDialog().isVisible = true
                    MasterFrame.itemPanel.reloadContent()
                }
            }

            JMenu("Importer depuis une autre scene").applyAndAppend(this) {
                for (scene in act!!.scenes) {
                    if (scene.id.value != act!!.sceneId) {
                        val itemMenu = JMenu(scene.name).apply {
                            for (token in scene.elements) {
                                val item = JMenuItem(token.name + " (" + token.type.name + ")").apply {
                                    addActionListener {
                                        transaction(DAO.database) { Scene.moveElementToScene(token, Scene[act!!.sceneId]) }
                                        ViewManager.repaint()
                                    }
                                }

                                this.add(item)
                            }
                        }

                        this.add(itemMenu)
                    }
                }
            }

            this.addSeparator()

            JMenuItem("Supprimer pion selectionné").applyAndAppend(this) {
                this.addActionListener {
                    SelectPanel.selectedElement?.let { element ->
                        ViewManager.removeToken(element)
                    }
                }
            }

            JMenu("Vider le plateau").applyAndAppend(this) {
                val areYouSure = JMenu("Vraiment?").apply {
                    val really = JMenuItem("Sûr?").apply {
                        addActionListener {
                            transaction(DAO.database) {
                                for (token in Scene[act!!.sceneId].elements) {
                                    println("test")
                                    ViewManager.removeToken(token)
                                    transaction(DAO.database) { token.delete() }
                                    Thread.sleep(100)
                                }
                            }
                        }
                    }
                    this.add(really)
                }
                this.add(areYouSure)
            }
        }
    }
}