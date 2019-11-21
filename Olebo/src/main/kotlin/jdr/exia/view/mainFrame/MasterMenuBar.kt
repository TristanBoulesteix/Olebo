package jdr.exia.view.mainFrame

import jdr.exia.controller.ViewManager
import jdr.exia.model.act.Act
import jdr.exia.model.dao.DAO
import jdr.exia.view.editor.elements.BlueprintDialog
import jdr.exia.view.homeFrame.HomeFrame
import org.jetbrains.exposed.sql.transactions.transaction
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
/*This is MasterFrame's menu bar (situated at the top)*/
object MasterMenuBar : JMenuBar() {
    var act: Act? = null

    init {

    }

    fun initialize() {
        this.removeAll()
        val actMenu = JMenu("Scenario")
        val closeAct = JMenuItem("Fermer scenario").apply {
            addActionListener {
                MasterFrame.isVisible = false; PlayerFrame.isVisible = false; HomeFrame().isVisible = true
            }
        }
        actMenu.add(closeAct)

        val pcFrameMenu = JMenu("Fenetre PJs")
        val togglePlayerFrame = JMenuItem("Fenetre PJs ON/OFF").apply {
            addActionListener {
                PlayerFrame.toggleDisplay()

            }
        }
        pcFrameMenu.add(togglePlayerFrame)
        this.add(pcFrameMenu)
        val sceneMenu = JMenu("Scene")
        val selectScene = JMenu("Choisir une Scene")

        if (act != null) {
            var i = 0
            for (scene in act!!.scenes) { //Pour chaque scene, on créé une option pour activer la scene

                i++
                if (scene.id.value == act!!.sceneId) {
                    val item = JMenuItem("$i ${scene.name} (Active)")
                    selectScene.add(item)
                } else {
                    val item = JMenuItem("$i ${scene.name}")
                    item.addActionListener { transaction(DAO.database) { ViewManager.changeCurrentScene(scene.id.value); } }
                    selectScene.add(item)
                }
            }
        }
        sceneMenu.add(selectScene)

        val tokenMenu = JMenu("Pions")
        val removeSelectedToken = JMenuItem("Supprimer pion selectionné").apply {
            addActionListener {
                SelectPanel.selectedElement?.let { it1 ->ViewManager.removeToken(it1)
                }
            }
        }
        val bpManagement = JMenuItem("Gèrer les Blueprints").apply { addActionListener {
            BlueprintDialog().isVisible = true
        }
        }
        val getTokenFromScene = JMenu("")



        tokenMenu.add(bpManagement)
        tokenMenu.add(removeSelectedToken)
        this.add(actMenu)
        this.add(tokenMenu)
        this.add(sceneMenu)
    }
}