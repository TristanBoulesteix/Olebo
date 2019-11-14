package jdr.exia.controller

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.element.Element
import jdr.exia.model.element.Position
import jdr.exia.view.mainFrame.MasterMenuBar
import jdr.exia.view.mainFrame.ViewFacade

object ViewManager {
    private var activeAct: Act? = null
    private var activeScene: Scene? = null
    private var grabbedToken: Element? = null
    init {}

    fun clickNDrop(x: Int, y: Int) {
        /*If a token has already been grabbed, then it is placed with dropToken(),
        else tries to find a token where the screen was clicked with  loadTokenFromClick(x,y)*/
        if (grabbedToken == null) {
            grabbedToken = getTokenFromXY(x, y)
            if (grabbedToken != null) {
                ViewFacade.addMarker(grabbedToken!!)
                this.repaint()
            }

        } else {
            dropToken(x, y)
            this.repaint()
        }
    }

    fun initializeAct(act: Act) {
        activeAct = act
        MasterMenuBar.act = act
        MasterMenuBar.initialize()
        loadCurrentScene()
    }

    fun removeToken(token: Element) { //removes given token from MutableList
        activeScene?.elements?.remove(token)
    }

    fun changeCurrentScene(sceneId: Int) {
        activeAct!!.sceneId = sceneId
        loadCurrentScene()
        repaint()
    }


    private fun loadCurrentScene() {
        with(activeAct) {
            activeScene = this!!.scenes.findWithId(id = activeAct!!.sceneId)
            ViewFacade.setMapBackground(activeScene!!.background)
            ViewFacade.setSelectedToken(null)
            ViewManager.repaint()
            ViewFacade.turnVisible()
        }
    }

    fun moveToken(
        x: Int,
        y: Int
    ) { //Changes a token's position without dropping it (a moved token stays selected) , intended for small steps
        if (grabbedToken != null) {
            val newX = (x - (grabbedToken!!.hitBox.width / 2))
            val newY = (y - (grabbedToken!!.hitBox.height / 2))
            grabbedToken!!.position = Position(newX, newY)
            ViewFacade.addMarker(grabbedToken!!)
            repaint()
        }
    }

    private fun dropToken(
        x: Int,
        y: Int
    ) { /* Places the currently grabbed token to last click's location, and drops it*/

        val newX = (x - (grabbedToken!!.hitBox.width / 2))
        val newY = (y - (grabbedToken!!.hitBox.height / 2))
        grabbedToken!!.position = Position(newX, newY)
        grabbedToken = null
        ViewFacade.removeMarker()
        repaint()


    }

    private fun addToken(token: Element) { //Adds a single token to this object's Token list
        this.activeScene?.elements?.add(token)
    }

    private fun getTokenFromXY(
        x: Int,
        y: Int
    ): Element? { //Receives a clicked point (x,y), returns the first soken found in the Tokens array, or null if none matched
        activeScene!!.elements.forEach {
            if (it.hitBox.contains(x, y)) {
                return (it)
            }
        }
        return null
    }


    fun repaint() {
        updateTokens()
        ViewFacade.repaintFrames()
    }

    fun selectToken(
        x: Int,
        y: Int
    ) { //cheks if the point taken was on a token, if it is, transmits it to SelectPanel to display the token's characteristics
        var selected = getTokenFromXY(x, y)
        if (selected != null) {
            ViewFacade.setSelectedToken(selected)
            repaint()
        }
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