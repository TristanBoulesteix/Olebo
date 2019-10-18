package jdr.exia.view.mainFrame

import jdr.exia.model.act.Act
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
        val closeAct = JMenuItem("changeAct")
        actMenu.add(closeAct)



        val sceneMenu = JMenu("Scene")
        var selectScene = JMenu("SelectScene")
            if (act != null){
                var i = 0
                for(scene in act!!.scenes){
                    i++
                    selectScene.add(JMenuItem("$i ${scene.name}").apply { act!!.sceneId= scene.id.value})
                }
            }

        val changeScene = JMenu("selectScene")

        sceneMenu.add(changeScene)

        val tokenMenu = JMenu("Token")
        val addToken = JMenuItem("addToken").apply { addActionListener{ println("k")} }
        val removeSelectedToken = JMenuItem("Remove Selected Item").apply { addActionListener{ println("helpoutjjjj")} }
        tokenMenu.add(addToken)
        tokenMenu.add(removeSelectedToken)





        this.add(actMenu)
        this.add(tokenMenu)

    }

}