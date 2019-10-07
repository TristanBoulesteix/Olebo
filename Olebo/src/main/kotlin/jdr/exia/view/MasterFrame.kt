package jdr.exia.view

import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JFrame
import jdr.exia.model.element.Element
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JPanel
import kotlin.system.exitProcess

/*MasterFrame is the Game Master's Interface, it contains a Map panel (the same as PlayerFrame), an ItemPanel and a SelectPanel.
 * MasterFrame will be focused most of the time, so it contains all KeyListeners for the program
this is a singleton*/

/* TODO: create the Drag and drop system
 *
 * Idea on how this works:
 * The
 * */
object MasterFrame : JFrame(), KeyListener, gameFrame {


    private var masterFramePanel = JPanel()
    private val mapPanel = MapPanel()
    var selectPanel = SelectPanel // Will contain all info on selected Item
    var itemPanel = ItemPanel // Will contain list of available items


    override fun setMapBackground(imageName: String) {
        this.mapPanel.backGroundImage = ImageIO.read(Element::class.java.getResource(imageName).openStream())

    }

    init {



        val screens = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
        this.setSize(screens[0].displayMode.width,screens[0].displayMode.height)






        this.title = "Master"

        addKeyListener(this)
        this.defaultCloseOperation = EXIT_ON_CLOSE




        masterFramePanel.size = this.size
        masterFramePanel.background = Color.GRAY
        masterFramePanel.layout = GridBagLayout()
        contentPane = masterFramePanel


        mapPanel.setSize(1280, 720)

        selectPanel.setSize(100, 100)
        selectPanel.background = Color.green

        itemPanel.setSize(100, 100)
        itemPanel.background = Color.yellow

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
      println("haha")


    }

    override fun keyPressed(keyEvent: KeyEvent) {

        if (keyEvent.keyCode == KeyEvent.VK_ESCAPE) {

            this.dispose()

            exitProcess(0)
        }
    }

    override fun keyReleased(keyEvent: KeyEvent) {

    }

    override fun updateMap(tokens: MutableList<Element>){
        this.mapPanel.updateTokens(tokens)

    }







}
