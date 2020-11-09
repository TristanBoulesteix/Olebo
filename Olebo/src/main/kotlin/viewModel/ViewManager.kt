package viewModel

import model.act.Act
import model.act.Scene
import model.dao.DAO
import model.element.Blueprint
import model.element.Element
import model.element.Position
import view.rpgFrames.MasterFrame
import view.rpgFrames.MasterMenuBar
import view.rpgFrames.ViewFacade
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Rectangle

/**
 * Manage MasterFrame and PlayerFrame
 */
object ViewManager {
    private var activeAct: Act? = null
    private var activeScene: Scene? = null

    private var selectedElement: Element? = null

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
        selectedElement = null
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
        if (selectedElement != null) {
            val newX = (x - (selectedElement!!.hitBox.width / 2))
            val newY = (y - (selectedElement!!.hitBox.height / 2))
            selectedElement!!.position = Position(newX, newY)
            repaint()
        }
    }

    private fun unSelectElements() {
        selectedElement = null
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
        selectedElement = getTokenFromXY(x, y)
        if (selectedElement != null) {
            ViewFacade.setSelectedToken(selectedElement!!)
            repaint()
        } else {
            unSelectElements()
        }
    }

    fun selectElements(rec: Rectangle) {
        val selectedElements = mutableListOf<Element>()

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
    }

    fun selectUp() { //TODO: Bug, mais pas prioritaire
        if (selectedElement == null && activeScene!!.elements.size > 0) {
            selectedElement = activeScene!!.elements[0]
        } else if (activeScene!!.elements.getOrNull(activeScene!!.elements.indexOf(selectedElement) + 1) != null) {
            selectedElement = activeScene!!.elements[activeScene!!.elements.indexOf(selectedElement) + 1]
        }
        ViewFacade.setSelectedToken(selectedElement)
        repaint()
    }

    fun selectDown() { //TODO: Bug
        if (selectedElement == null && activeScene!!.elements.size > 0) {
            selectedElement = activeScene!!.elements[0]
        } else if (activeScene!!.elements.getOrNull(activeScene!!.elements.indexOf(selectedElement) - 1) != null) {
            println("je fonctionne")
            selectedElement = activeScene!!.elements[activeScene!!.elements.indexOf(selectedElement) - 1]
        }
        ViewFacade.setSelectedToken(selectedElement)
        repaint()
    }

    private fun updateTokens() { //Updates the tokens on the maps by repainting everything
        ViewFacade.placeTokensOnMaps(activeScene!!.elements)
    }

    fun toggleVisibility(token: Element, visibility: Boolean? = null) {
        token.isVisible = visibility ?: !token.isVisible
        repaint()
    }
}
