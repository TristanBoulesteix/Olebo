package jdr.exia.view

import jdr.exia.localization.STR_PLAYER_TITLE_FRAME
import jdr.exia.localization.StringLocale
import jdr.exia.view.composable.master.MapPanel
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.tools.event.addKeyPressedListener
import java.awt.Window
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JDialog

/**
 * PlayerFrame is the Frame the Players can see, it shares its content with MasterFrame
 */
class PlayerWindow(mapPanel: MapPanel, private val onHide: DefaultFunction) : JDialog(null as Window?) {
    init {
        this.contentPane = mapPanel
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE

        this.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) = dispose()
        })

        this.addKeyPressedListener {
            if (it.keyCode == KeyEvent.VK_ESCAPE) {
                dispose()
            }
        }
    }

    override fun setTitle(title: String) =
        super.setTitle("Olebo - ${StringLocale[STR_PLAYER_TITLE_FRAME]} - \"$title\"")

    override fun dispose() {
        super.dispose()
        onHide()
    }
}