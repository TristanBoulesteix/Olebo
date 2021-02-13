package jdr.exia.view.frames.rpg

import jdr.exia.localization.STR_DM_TITLE_FRAME
import jdr.exia.localization.Strings
import jdr.exia.model.utils.Elements
import jdr.exia.model.utils.emptyElements
import jdr.exia.view.utils.DIMENSION_FRAME
import jdr.exia.view.utils.event.addKeyPressedListener
import jdr.exia.view.utils.gridBagConstraintsOf
import jdr.exia.viewModel.ViewManager
import java.awt.Color
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JPanel

/**
 * MasterFrame is the Game Master's Interface, it contains a Map panel (the same as PlayerFrame, but scaled down), an ItemPanel and a SelectPanel.
 * MasterFrame will be focused most of the time, so it contains all KeyListeners for the program
 * this is a singleton
 */
object MasterFrame : JFrame(), GameFrame {
    /**
     * Main [JPanel] that contains other panels
     */
    private var masterFramePanel = JPanel()

    /**
     * this [MasterFrame] frame's mapPanel
     */
    val mapPanel = MapPanel(this)

    /**
     * Will contain list of available items
     */
    var itemPanel = ItemPanel()

    private var selectPanel = SelectPanel()

    var selectedElements: Elements = emptyElements()
        set(value) {
            selectPanel.selectedElements = value
            mapPanel.selectedElements = value
            field = value
        }

    override var mapBackground by mapPanel::backGroundImage

    override fun setTitle(title: String) {
        super.setTitle("Olebo - ${Strings[STR_DM_TITLE_FRAME]} - \"$title\"")
    }

    init {
        this.extendedState = MAXIMIZED_BOTH
        this.size = DIMENSION_FRAME
        this.isFocusable = true
        this.addKeyPressedListener {
            when (it.keyCode) {
                KeyEvent.VK_UP -> ViewManager.selectUp()
                KeyEvent.VK_DOWN -> ViewManager.selectDown()
                KeyEvent.VK_RIGHT -> ViewManager.rotateRight()
                KeyEvent.VK_LEFT -> ViewManager.rotateLeft()
            }
        }
        this.defaultCloseOperation = EXIT_ON_CLOSE
        masterFramePanel.apply {
            size = this.size
            background = Color.GRAY
            layout = GridBagLayout()
        }
        contentPane = masterFramePanel

        mapPanel.setSize(1280, 720)

        itemPanel.apply {
            size = Dimension(this.width - 1280, this.height)
            background = Color.yellow
        }

        selectPanel.setSize(mapPanel.width, (this.height - mapPanel.height))

        val itemConstraints = gridBagConstraintsOf(
            gridx = 0,
            gridy = 0,
            gridHeight = 2,
            weightx = 1.0,
            weighty = 2.0,
            fill = GridBagConstraints.BOTH
        )
        val selectConstraints = gridBagConstraintsOf(
            gridx = 1,
            gridy = 1,
            gridWidth = GridBagConstraints.REMAINDER,
            weightx = 0.5,
            weighty = 1.0,
            fill = GridBagConstraints.BOTH
        )
        val mapConstraints = gridBagConstraintsOf(
            gridx = 3,
            gridy = 0,
            weightx = 3.0,
            weighty = 7.0,
            fill = GridBagConstraints.BOTH
        )

        masterFramePanel.add(mapPanel, mapConstraints)
        masterFramePanel.add(itemPanel, itemConstraints)
        masterFramePanel.add(selectPanel, selectConstraints)
        jMenuBar = MasterMenuBar
    }

    override fun reload() {
        mapPanel.repaint()
        itemPanel.reload()
        selectPanel.reload()
    }

    override fun updateMap(tokens: Elements) {
        mapPanel.updateTokens(tokens)
    }

    override fun dispose() {
        PlayerFrame.hide()
        super.dispose()
    }

    override fun requestFocus() {
        this.toFront()
        super.requestFocus()
    }
}
