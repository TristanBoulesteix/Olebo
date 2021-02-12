package jdr.exia.viewModel

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.*
import jdr.exia.model.utils.*
import jdr.exia.view.frames.rpg.MasterFrame
import jdr.exia.view.frames.rpg.MasterMenuBar
import jdr.exia.view.frames.rpg.PlayerFrame
import jdr.exia.view.frames.rpg.ViewFacade
import jdr.exia.view.utils.getTokenFromPosition
import jdr.exia.view.utils.positionOf
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Rectangle

/**
 * Manage MasterFrame and PlayerFrame
 */
object ViewManager {
    const val ABSOLUTE_WIDTH = 1600
    const val ABSOLUTE_HEIGHT = 900

    private var activeAct: Act? = null
    private var activeScene: Scene? = null

    private var selectedElements = mutableEmptyElements()

    var cursorPosition: Position? = null

    /**
     * Get the list of all blueprints
     */
    val items
        get() = Blueprint.all()

    suspend fun initializeAct(idAct: Int) = coroutineScope {
        val act = transaction { Act[idAct] }
        yield()
        activeAct = act
        withContext(Dispatchers.Main) {
            MasterMenuBar.act = act
            MasterMenuBar.initialize()
            yield()
            loadCurrentScene()
            yield()
            PlayerFrame.toggle(Settings.playerFrameOpenedByDefault)
        }
    }

    fun removeSelectedElements() = removeElements(selectedElements)

    fun removeElements(elements: Elements) { //removes given token from MutableList
        selectedElements = mutableEmptyElements()
        ViewFacade.setSelectedToken(null)
        activeScene.callCommandManager(elements, Element::cmdDelete)
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

    /**
     * Changes tokens position without dropping them (a moved token stays selected), intended for small steps
     */
    fun moveTokens(position: Position, originPosition: Position? = null) {
        val origin = originPosition?.let { pos ->
            selectedElements.find { it.hitBox in pos } ?: selectElement(pos).let { selectedElements.firstOrNull() }
        }

        /**
         * Return a new [Position] inside the bourders of the map
         */
        fun Position.checkBound(): Position {
            var newPosition = this

            if (position.x < 0) {
                newPosition = newPosition.copy(x = 0)
            } else if (newPosition.x > ABSOLUTE_WIDTH) {
                newPosition = newPosition.copy(x = ABSOLUTE_WIDTH)
            }

            if (newPosition.y < 0) {
                newPosition = newPosition.copy(y = 0)
            } else if (newPosition.y > ABSOLUTE_HEIGHT) {
                newPosition = newPosition.copy(y = ABSOLUTE_HEIGHT)
            }

            return newPosition
        }

        val newPosition = position.checkBound()

        if (selectedElements.isNotEmpty()) {
            if (selectedElements.size == 1) {
                selectedElements.first()
                    .cmdPosition(selectedElements.first().positionOf(newPosition), activeScene!!.commandManager)
            } else {
                val newPositions = mutableListOf<Position>()

                val originElement = (selectedElements.find { it === origin } ?: selectedElements.first())

                newPositions += originElement.positionOf(newPosition)

                val diffPosition = newPosition - originElement.position

                selectedElements.filterNot { it === originElement }.forEach {
                    newPositions += it.positionOf((it.position + diffPosition).checkBound())
                }

                activeScene.callCommandManager(newPositions, selectedElements, Element::cmdPosition)
            }
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
    fun selectElement(position: Position) {
        selectedElements = activeScene!!.elements.getTokenFromPosition(position)?.toElements()?.toMutableList()
            ?: mutableEmptyElements()
        if (selectedElements.isNotEmpty()) {
            ViewFacade.setSelectedToken(selectedElements[0])
            repaint()
        } else {
            unSelectElements()
        }
    }

    fun positionHasElement(position: Position) =
        activeScene!!.elements.getTokenFromPosition(position)?.toElements() != null

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
        activeScene.callCommandManager(
            visibility ?: if (tokens.size == 1) !tokens.first().isVisible else true,
            tokens,
            Element::cmdVisiblity
        )
        repaint()
    }

    fun rotateRight() = activeScene.callCommandManager(selectedElements, Element::cmdOrientationToRight).also { repaint() }

    fun rotateLeft() = activeScene.callCommandManager(selectedElements, Element::cmdOrientationToLeft).also { repaint() }

    fun updatePriorityToken(priority: Priority) = selectedElements.forEach {
        it.priority = priority
    }.also { repaint() }

    fun updateSizeToken(size: Size) =
        activeScene.callCommandManager(size, selectedElements, Element::cmdDimension).also { repaint() }

    fun updateLabel(label: String) =
        selectedElements.forEach { transaction { it.alias = label } }
}
