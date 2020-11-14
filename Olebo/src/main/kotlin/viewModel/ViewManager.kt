package viewModel

import model.act.Act
import model.act.Scene
import model.dao.DAO
import model.element.*
import model.utils.Elements
import model.utils.doIfContainsSingle
import model.utils.emptyElementsList
import model.utils.toElements
import view.frames.rpg.MasterFrame
import view.frames.rpg.MasterMenuBar
import view.frames.rpg.ViewFacade
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Rectangle

/**
 * Manage MasterFrame and PlayerFrame
 */
object ViewManager {
    private var activeAct: Act? = null
    private var activeScene: Scene? = null

    private var selectedElements = emptyElementsList()

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
    }

    fun removeToken(token: Element) { //removes given token from MutableList
        selectedElements = emptyElementsList()
        ViewFacade.setSelectedToken(null)
        activeScene?.elements?.remove(token)
        transaction(DAO.database) { token.delete() }
        repaint()
    }

    fun changeCurrentScene(sceneId: Int) {
        activeAct!!.sceneId = sceneId
        loadCurrentScene()
        repaint()
    }

    private fun loadCurrentScene() {
        with(activeAct) activeAct@{
            activeScene = this!!.scenes.findWithId(this@activeAct.sceneId)
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

    fun moveToken(x: Int, y: Int) { //Changes a token's position without dropping it (a moved token stays selected) , intended for small steps
        if (selectedElements.isNotEmpty() && selectedElements.size == 1) {
            val newX = (x - (selectedElements[0].hitBox.width / 2))
            val newY = (y - (selectedElements[0].hitBox.height / 2))
            selectedElements[0].position = Position(newX, newY)
            repaint()
        }
    }

    private fun unSelectElements() {
        selectedElements = emptyElementsList()
        ViewFacade.unSelectElements()
        repaint()
    }

    fun addToken(token: Blueprint) { //Adds a single token to this object's Token list
        activeScene?.addElement(token)
        repaint()
    }

    private fun getTokenFromXY(x: Int, y: Int): Element? {//Receives a clicked point (x,y), returns the first soken found in the Tokens array, or null if none matched
        activeScene!!.elements.forEach {
            if (it.hitBox.contains(x, y)) {
                return it
            }
        }
        return null
    }

    fun repaint() {
        updateTokens()
        ViewFacade.repaintFrames()
    }

    fun selectElement(x: Int, y: Int) { //cheks if the point taken was on a token, if it is, transmits it to SelectPanel to display the token's characteristics
        selectedElements = getTokenFromXY(x, y)?.toElements() ?: emptyElementsList()
        if (selectedElements.isNotEmpty()) {
            ViewFacade.setSelectedToken(selectedElements[0])
            repaint()
        } else {
            unSelectElements()
        }
    }

    fun selectElements(rec: Rectangle) {
        val selectedElements = emptyElementsList()

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
        selectedElements = if (selectedElements.isEmpty() && this.elements.size > 0) {
            this.elements[0].toElements()
        } else {
            fun Int.plusOne(list: Elements) = if (this == list.size - 1) 0 else this + 1

            selectedElements.doIfContainsSingle { element ->
                if (this.elements.getOrNull(this.elements.indexOfFirst { it.id == element.id }.plusOne(this.elements)) != null) {
                    this.elements[this.elements.indexOfFirst { it.id == element.id }.plusOne(this.elements)].toElements()
                } else emptyElementsList()
            } ?: emptyElementsList()
        }

        ViewFacade.setSelectedToken(*selectedElements.toTypedArray())
        repaint()
    }

    fun selectDown() {
        with(activeScene!!) {
            selectedElements = if (selectedElements.isEmpty() && this.elements.size > 0) {
                this.elements[0].toElements()
            } else {
                fun Int.minusOne(list: Elements) = if (this == 0) list.size - 1 else this - 1

                selectedElements.doIfContainsSingle { element ->
                    if (this.elements.getOrNull(this.elements.indexOfFirst { it.id == element.id }.minusOne(this.elements)) != null) {
                        activeScene!!.elements[this.elements.indexOfFirst { it.id == element.id }.minusOne(this.elements)].toElements()
                    } else emptyElementsList()
                } ?: emptyElementsList()
            }

            ViewFacade.setSelectedToken(*selectedElements.toTypedArray())
            repaint()
        }
    }

    private fun updateTokens() { //Updates the tokens on the maps by repainting everything
        ViewFacade.placeTokensOnMaps(activeScene!!.elements)
    }

    fun toggleVisibility(token: Element, visibility: Boolean? = null) {
        token.isVisible = visibility ?: !token.isVisible
        repaint()
    }

    fun rotateRight() = selectedElements.forEach {
        it.rotateRight()
    }.also { repaint() }

    fun rotateLeft() = selectedElements.forEach {
        it.rotateLeft()
    }.also { repaint() }
}
