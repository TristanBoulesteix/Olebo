package jdr.exia.controller

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.dao.DAO
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.Position
import jdr.exia.view.mainFrame.MasterMenuBar
import jdr.exia.view.mainFrame.ViewFacade
import org.jetbrains.exposed.sql.transactions.transaction

object ViewManager {
    private var activeAct: Act? = null
    private var activeScene: Scene? = null

    private var selectedElement: Element? = null
    init {}

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
        with(activeAct) {
            activeScene = this!!.scenes.findWithId(id = activeAct!!.sceneId)
            ViewFacade.loadItems()
            ViewFacade.setMapBackground(activeScene!!.background)
            ViewFacade.setSelectedToken(null)
            repaint()
            ViewFacade.turnVisible()
        }
    }

    fun moveToken(x: Int,y: Int) { //Changes a token's position without dropping it (a moved token stays selected) , intended for small steps
        if (selectedElement!= null) {
            val newX = (x - (selectedElement!!.hitBox.width / 2))
            val newY = (y - (selectedElement!!.hitBox.height / 2))
            selectedElement!!.position = Position(newX, newY)
            repaint()
        }
    }

    fun unSelectElement(){
        selectedElement = null
        ViewFacade.unSelectElement()
        repaint()
    }

    fun addToken(token: Blueprint) { //Adds a single token to this object's Token list
        this.activeScene?.addElement(token)
        this.repaint()
    }

    private fun getTokenFromXY(x: Int,y: Int): Element? {//Receives a clicked point (x,y), returns the first soken found in the Tokens array, or null if none matched
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

    fun selectElement(x: Int,y: Int) { //cheks if the point taken was on a token, if it is, transmits it to SelectPanel to display the token's characteristics
        selectedElement = getTokenFromXY(x, y)
        if (selectedElement != null) {
            ViewFacade.setSelectedToken(selectedElement)
            repaint()
        }else{
            unSelectElement()}
    }

    fun selectUp(){ //TODO: Bug, mais pas prioritaire
        if(selectedElement == null && activeScene!!.elements.size > 0){
            selectedElement = activeScene!!.elements[0]
        } else if(activeScene!!.elements.getOrNull(activeScene!!.elements.indexOf(selectedElement)+1) != null){
            selectedElement = activeScene!!.elements[activeScene!!.elements.indexOf(selectedElement)+1]
        }
        ViewFacade.setSelectedToken(selectedElement)
        repaint()
    }

    fun selectDown(){ //TODO: Bug
        if(selectedElement == null && activeScene!!.elements.size > 0){
            selectedElement = activeScene!!.elements[0]
        } else if(activeScene!!.elements.getOrNull(activeScene!!.elements.indexOf(selectedElement)-1) != null){
            println("je fonctionne")
            selectedElement = activeScene!!.elements[activeScene!!.elements.indexOf(selectedElement)-1]
        }
        ViewFacade.setSelectedToken(selectedElement)
        repaint()
    }

    private fun updateTokens() { //Updates the tokens on the maps by repainting everything
        ViewFacade.placeTokensOnMaps(activeScene!!.elements)
    }

    fun toggleVisibility(token: Element?) {
        if (token != null) {
            token.isVisible = !token.isVisible
            repaint()
        }
    }
}
