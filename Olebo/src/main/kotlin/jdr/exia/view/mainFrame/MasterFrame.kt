package jdr.exia.view.mainFrame

import jdr.exia.controller.ViewManager
import jdr.exia.model.element.Element
import java.awt.Color
import java.awt.GraphicsEnvironment
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.system.exitProcess

/*MasterFrame is the Game Master's Interface, it contains a Map panel (the same as PlayerFrame, but scaled down), an ItemPanel and a SelectPanel.
 * MasterFrame will be focused most of the time, so it contains all KeyListeners for the program
this is a singleton*/

/* TODO:
 *
 * */
object MasterFrame : JFrame(), KeyListener, GameFrame {
    private var masterFramePanel = JPanel()
    val mapPanel = MapPanel()
    var selectPanel = SelectPanel // Will contain all info on selected Item
    var itemPanel = ItemPanel // Will contain list of available items

    override fun setMapBackground(imageName: String) {
        mapPanel.backGroundImage = ImageIO.read(File(imageName))
    }

    init {
        val screens = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
        this.setSize(screens[0].displayMode.width,screens[0].displayMode.height)
        if (screens.size == 1) { //If there is only 1 screen, we display both frames there
        } else {

        }
        this.title = "Master"
        addKeyListener(this)
        this.defaultCloseOperation = EXIT_ON_CLOSE
        masterFramePanel.size = this.size
        masterFramePanel.background = Color.GRAY
        masterFramePanel.layout = GridBagLayout()
        contentPane = masterFramePanel

        mapPanel.setSize(1280, 720)

        itemPanel.setSize(this.width-1280, this.height)
        itemPanel.background = Color.yellow



        selectPanel.setSize(this.mapPanel.width,(this.height - mapPanel.height))




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
        mapConstraints.weighty = 7.0
        mapConstraints.fill = GridBagConstraints.BOTH

        masterFramePanel.add(mapPanel, mapConstraints)
        masterFramePanel.add(itemPanel, itemConstraints)
        masterFramePanel.add(selectPanel, selectConstraints)
        this.mapPanel.isMasterMapPanel = true
        jMenuBar = MasterMenuBar

    }

    fun setMarker(token: Element){
        mapPanel.setMarker(token)
    }
    fun removeMarker(){
        mapPanel.clearMarker()
    }


    // KeyListener section, to add Key bindings
    override fun keyTyped(keyEvent: KeyEvent) {
        println(keyEvent.keyCode)
        when (keyEvent.keyCode) {
            KeyEvent.VK_ESCAPE -> { //remove after tesing is complete
                dispose()
                exitProcess(0)
            }
            KeyEvent.VK_UP -> {
                ViewManager.selectUp()
                println("esh")
            }
            KeyEvent.VK_DOWN -> {
                ViewManager.selectDown()
                println("ush")
            }
        }
    }

    override fun keyPressed(keyEvent: KeyEvent) {


    }

    override fun keyReleased(keyEvent: KeyEvent) {

    }

    override fun updateMap(tokens: MutableList<Element>){
        mapPanel.updateTokens(tokens)
    }
}
