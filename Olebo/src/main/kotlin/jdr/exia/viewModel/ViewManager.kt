package jdr.exia.viewModel

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.*
import jdr.exia.model.utils.*
import jdr.exia.view.frames.rpg.MasterFrame
import jdr.exia.view.frames.rpg.MasterMenuBar
import jdr.exia.view.frames.rpg.PlayerFrame
import jdr.exia.view.frames.rpg.ViewFacade
import jdr.exia.view.utils.getTokenFromPoint
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Point
import java.awt.Rectangle

/**
 * Manage MasterFrame and PlayerFrame
 */
object ViewManager {
    private var activeAct: Act? = null
    var activeScene: Scene? = null
        private set

    private var selectedElements = mutableEmptyElements()

    var cursorPoint: Point? = null

    /**
     * Get the list of all blueprints
     */
    val items
        get() = Blueprint.all()

    fun initializeAct(act: Act) {
        activeAct = act
        MasterMenuBar.act = act
        MasterMenuBar.initialize()
        loadCurrentScene()
        PlayerFrame.toggle(Settings.playerFrameOpenedByDefault)
        MasterFrame.requestFocus()
    }

    fun removeSelectedElements() = removeElements(selectedElements)

    fun removeElements(elements: Elements) { //removes given token from MutableList
        selectedElements = mutableEmptyElements()
        ViewFacade.setSelectedToken(null)
        activeScene.callManager(elements, Element::cmdDelete)
        repaint()
    }

    fun changeCurrentScene(sceneId: Int) {
        activeAct!!.sceneId = sceneId
        loadCurrentScene()
        repaint()
    }

    fun unselectAllElements() {
        selectedElements = mutableEmptyElements()
        ViewFacade.setSelectedToken(null)
    }

    private fun loadCurrentScene() {
        with(activeAct!!) activeAct@{
            activeScene = this.scenes.findWithId(this.sceneId)
            ViewFacade.apply {
                this.loadItems()
                this.setMapBackground(activeScene!!.background)
                this.setSelectedToken()
                this.actName = this@activeAct.name
                repaint()
                this.turnVisible()
            }
        }
    }

    fun moveToken(
        x: Int,
        y: Int
    ) { //Changes a token's position without dropping it (a moved token stays selected) , intended for small steps
        if (selectedElements.isNotEmpty() && selectedElements.size == 1) {
            val newX = (x - (selectedElements[0].hitBox.width / 2))
            val newY = (y - (selectedElements[0].hitBox.height / 2))
            selectedElements[0].cmdPosition(Position(newX, newY), activeScene!!.commandManager)
            repaint()
        }
    }

    private fun unSelectElements() {
        selectedElements = mutableEmptyElements()
        ViewFacade.unSelectElements()
        repaint()
    }

    fun addToken(token: Blueprint) { //Adds a single token to this object's Token list
        activeScene?.addElement(token)
        repaint()
    }

    fun repaint() {
        updateTokens()
        ViewFacade.reloadFrames()
    }

    /**
     * Checks if the point taken was on a token, if it is, transmits it to SelectPanel to display the token's characteristics
     */
    fun selectElement(x: Int, y: Int) {
        selectedElements = activeScene!!.elements.getTokenFromPoint(Point(x, y))?.toElements()?.toMutableList()
            ?: mutableEmptyElements()
        if (selectedElements.isNotEmpty()) {
            ViewFacade.setSelectedToken(selectedElements[0])
            repaint()
        } else {
            unSelectElements()
        }
    }

    fun selectElements(rec: Rectangle) {
        val selectedElements = mutableEmptyElements()

        activeScene!!.elements.forEach {
            if (rec.contains(MasterFrame.mapPanel.getRelativeRectangleOfToken(it))) {
                selectedElements += it
            }
        }

        if (selectedElements.isNotEmpty()) {
            ViewFacade.setSelectedToken(*selectedElements.toTypedArray())
            repaint()
        } else {
            unSelectElements()
        }

        this.selectedElements = selectedElements
    }

    fun selectUp() = with(activeScene!!) {
        selectedElements = if (selectedElements.isEmpty() && this.elements.isNotEmpty()) {
            this.elements[0].toElements().toMutableList()
        } else {
            fun Int.plusOne(list: Elements) = if (this == list.size - 1) 0 else this + 1

            selectedElements.doIfContainsSingle { element ->
                if (this.elements.getOrNull(this.elements.indexOfFirst { it.id == element.id }
                        .plusOne(this.elements)) != null) {
                    this.elements[this.elements.indexOfFirst { it.id == element.id }
                        .plusOne(this.elements)].toElements().toMutableList()
                } else mutableEmptyElements()
            } ?: mutableEmptyElements()
        }

        ViewFacade.setSelectedToken(*selectedElements.toTypedArray())
        repaint()
    }

    fun selectDown() {
        with(activeScene!!) {
            selectedElements = if (selectedElements.isEmpty() && this.elements.isNotEmpty()) {
                this.elements[0].toElements().toMutableList()
            } else {
                fun Int.minusOne(list: Elements) = if (this == 0) list.size - 1 else this - 1

                selectedElements.doIfContainsSingle { element ->
                    if (this.elements.getOrNull(this.elements.indexOfFirst { it.id == element.id }
                            .minusOne(this.elements)) != null) {
                        activeScene!!.elements[this.elements.indexOfFirst { it.id == element.id }
                            .minusOne(this.elements)].toElements().toMutableList()
                    } else mutableEmptyElements()
                } ?: mutableEmptyElements()
            }

            ViewFacade.setSelectedToken(*selectedElements.toTypedArray())
            repaint()
        }
    }

    private fun updateTokens() { //Updates the tokens on the maps by repainting everything
        ViewFacade.placeTokensOnMaps(activeScene!!.elements)
    }

    fun toggleVisibility(tokens: Elements, visibility: Boolean? = null) {
        activeScene.callManager(
            visibility ?: if (tokens.size == 1) !tokens[0].isVisible else true,
            tokens,
            Element::cmdVisiblity
        )
        repaint()
    }

    fun rotateRight() = activeScene.callManager(selectedElements, Element::cmdOrientationToRight).also { repaint() }

    fun rotateLeft() = activeScene.callManager(selectedElements, Element::cmdOrientationToLeft).also { repaint() }

    fun updatePriorityToken(priority: Priority) = selectedElements.forEach {
        it.priority = priority
    }.also { repaint() }

    fun updateSizeToken(size: Size) =
        activeScene.callManager(size, selectedElements, Element::cmdDimension).also { repaint() }

    fun updateLabel(label: String) =
        selectedElements.forEach { transaction { it.alias = label } }.also { repaint() }
}
