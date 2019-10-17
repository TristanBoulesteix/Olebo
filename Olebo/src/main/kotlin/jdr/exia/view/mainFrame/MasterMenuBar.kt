package jdr.exia.view.mainFrame

import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

object MasterMenuBar: JMenuBar() {

    init{
        var menu = JMenu("Act")
        var testitem = JMenuItem("Ceci est futile")
        menu.add(testitem)
        this.add(menu)

    }
}