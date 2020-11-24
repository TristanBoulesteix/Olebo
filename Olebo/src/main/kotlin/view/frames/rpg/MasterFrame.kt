package view.frames.rpg

import model.utils.Elements
import view.utils.DIMENSION_FRAME
import viewModel.ViewManager
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.JPanel

/**
 * MasterFrame is the Game Master's Interface, it contains a Map panel (the same as PlayerFrame, but scaled down), an ItemPanel and a SelectPanel.
 * MasterFrame will be focused most of the time, so it contains all KeyListeners for the program
 * this is a singleton
 */
object MasterFrame : JFrame(), KeyListener, GameFrame {
    private var masterFramePanel = JPanel() // Main JPanel that contains other panels

    val mapPanel = MapPanel(true) //this frame's mapPanel

    var itemPanel = ItemPanel() // Will contain list of available items

    override fun setMapBackground(imageName: String) { //set the background image for the mappanels
        mapPanel.backGroundImage = ImageIO.read(File(imageName))
    }

    override fun setTitle(title: String) {
        super.setTitle("Olebo - Fenêtre MJ - \"$title\"")
    }

    init {
        this.extendedState = MAXIMIZED_BOTH
        this.size = DIMENSION_FRAME
        this.isFocusable = true
        addKeyListener(this)
        this.defaultCloseOperation = EXIT_ON_CLOSE
        masterFramePanel.size = this.size
        masterFramePanel.background = Color.GRAY
        masterFramePanel.layout = GridBagLayout()
        contentPane = masterFramePanel

        mapPanel.setSize(1280, 720)

        itemPanel.setSize(this.width - 1280, this.height)
        itemPanel.background = Color.yellow

        SelectPanel.setSize(mapPanel.width, (this.height - mapPanel.height))

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
        masterFramePanel.add(SelectPanel, selectConstraints)
        jMenuBar = MasterMenuBar

        mapPanel.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(me: MouseEvent) {
                ViewManager.cursorPoint = mapPanel.getAbsolutePoint(me.point)
            }
        })
    }

    override fun reload() {
        mapPanel.repaint()
        itemPanel.reloadContent()
        SelectPanel.reload()
    }

    // KeyListener section, to add Key bindings
    override fun keyTyped(keyEvent: KeyEvent) = Unit

    override fun keyPressed(keyEvent: KeyEvent) {
        when (keyEvent.keyCode) {
            KeyEvent.VK_UP -> ViewManager.selectUp()
            KeyEvent.VK_DOWN -> ViewManager.selectDown()
            KeyEvent.VK_RIGHT -> ViewManager.rotateRight()
            KeyEvent.VK_LEFT -> ViewManager.rotateLeft()
        }
    }

    override fun keyReleased(keyEvent: KeyEvent) = Unit

    override fun updateMap(tokens: Elements) {
        mapPanel.updateTokens(tokens)
    }

    override fun dispose() {
        PlayerFrame.hide()
        super.dispose()
    }
}
