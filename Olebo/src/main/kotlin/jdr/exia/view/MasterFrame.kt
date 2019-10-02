package jdr.exia.view

import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.system.exitProcess

/*MasterFrame is the Game Master's Interface, it contains a Map panel (the same as PlayerFrame, but scaled down), an ItemPanel and a SelectPanel.
 * MasterFrame will be focused most of the time, so it contains all KeyListeners for the program
this is a singleton*/

/* TODO: create the Drag and drop system
 * Idea on how this works:
 * The
 * */
object MasterFrame : JFrame(), KeyListener {
    private val masterFramePanel: JPanel = JPanel(GridBagLayout())

    val mapPanel = MapPanel
    val selectPanel = SelectPanel // Will contain all info on selected Item
    val itemPanel = ItemPanel // Will contain list of available items

    init {
        this.title = "Master"
        this.setSize(1936, 1056)
        addKeyListener(this)
        this.defaultCloseOperation = EXIT_ON_CLOSE

        mapPanel.setSize(1280, 720)

        selectPanel.setSize(100, 100)

        selectPanel.background = Color.green

        itemPanel.setSize(100, 100)
        itemPanel.background = Color.yellow


        masterFramePanel!!.size = this.size
        masterFramePanel!!.background = Color.GRAY
        this.contentPane = masterFramePanel

        val mapConstraints = GridBagConstraints()
        val itemConstraints = GridBagConstraints()
        val selectConstraints = GridBagConstraints()

        itemConstraints.gridx = 0
        itemConstraints.gridy = 0
        itemConstraints.gridheight = 2
        itemConstraints.weightx = 1.0
        itemConstraints.weighty = 2.0
        itemConstraints.fill = GridBagConstraints.BOTH

        selectConstraints.gridx = 1
        selectConstraints.gridy = 1
        selectConstraints.weightx = 0.5
        selectConstraints.weighty = 1.0
        selectConstraints.gridwidth = GridBagConstraints.REMAINDER
        selectConstraints.fill = GridBagConstraints.BOTH

        mapConstraints.gridx = 3
        mapConstraints.gridy = 0
        mapConstraints.weightx = 3.0
        mapConstraints.weighty = 5.0
        mapConstraints.fill = GridBagConstraints.BOTH

        masterFramePanel.add(mapPanel, mapConstraints)
        masterFramePanel.add(itemPanel, itemConstraints)
        masterFramePanel.add(selectPanel, selectConstraints)
    }

    // KeyListener section, to add Key bindings
    override fun keyTyped(keyEvent: KeyEvent) {
        TODO("Auto-generated method stub")

    }

    override fun keyPressed(keyEvent: KeyEvent) {
        // TODO("Auto-generated method stub")
        if (keyEvent.keyCode == KeyEvent.VK_ESCAPE) {
            this.dispose()
            exitProcess(0)
        }
    }

    override fun keyReleased(keyEvent: KeyEvent) {
        TODO("Auto-generated method stub")
    }
}
