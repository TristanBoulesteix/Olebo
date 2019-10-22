package jdr.exia.view.mainFrame

import jdr.exia.controller.ViewManager
import jdr.exia.model.act.Act
import jdr.exia.model.dao.DAO
import jdr.exia.view.homeFrame.ActSelectorPanel
import jdr.exia.view.homeFrame.HomeFrame
import org.jetbrains.exposed.sql.transactions.transaction
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

object MasterMenuBar: JMenuBar() {

    var act: Act? = null


    init{
       initialize()
    }

    fun initialize(){
        this.removeAll()
        val actMenu = JMenu("Act")
        val closeAct = JMenuItem("Close Act").apply { addActionListener{ MasterFrame.isVisible = false; PlayerFrame.isVisible = false; HomeFrame().isVisible = true} }
        actMenu.add(closeAct)

        val pcFrameMenu = JMenu("Player Frame")
        val togglePlayerFrame = JMenuItem("Toggle Player Frame").apply {
            addActionListener{
                PlayerFrame.toggleDisplay()

            } }
        pcFrameMenu.add(togglePlayerFrame)
        this.add(pcFrameMenu)
        val sceneMenu = JMenu("Scene")
        var selectScene = JMenu("Select a Scene")

            if (act != null){
                var i = 0
                for(scene in act!!.scenes){

                    i++
                    if (scene.id.value == act!!.sceneId){var item =JMenuItem("$i ${scene.name} (Active)")
                        selectScene.add(item)
                    }
                    else{

                    var item =JMenuItem("$i ${scene.name}")
                    item.addActionListener{ transaction(DAO.database){act!!.sceneId= scene.id.value; ViewManager.loadCurrentScene()} }
                        selectScene.add(item)
                    }

                }
            }



        sceneMenu.add(selectScene)

        val tokenMenu = JMenu("Token")
        val addToken = JMenuItem("Add a Token").apply { addActionListener{ println("k")} }
        val removeSelectedToken = JMenuItem("Remove Selected Item").apply { addActionListener{ SelectPanel.selectedElement?.let { it1 ->
            ViewManager.removeToken(it1)
        } } }
        tokenMenu.add(addToken)
        tokenMenu.add(removeSelectedToken)





        this.add(actMenu)
        this.add(tokenMenu)
        this.add(sceneMenu)

    }

}