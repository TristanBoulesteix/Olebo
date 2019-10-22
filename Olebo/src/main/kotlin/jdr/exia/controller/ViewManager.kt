package jdr.exia.controller

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.element.Element
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
                updateTokens()
            }

        } else {
            dropToken(x, y)
        }
    }

    fun initializeAct(act: Act) {
        activeAct = act
        MasterMenuBar.act = act
        MasterMenuBar.initialize()
        loadCurrentScene()
    }

    fun removeToken(token: Element) { //removes given token from MutableList
        activeScene?.elements?.remove(token) //TODO: replace mapToken with activeScene.elements
    }

    fun changeCurrentScene(sceneId: Int) {
        activeAct!!.sceneId = sceneId
        loadCurrentScene()
        updateTokens()
    }


    fun loadCurrentScene() {
        with(activeAct) {
            activeScene = this!!.scenes.findWithId(activeAct!!.sceneId)

            ViewFacade.setMapBackground(activeScene!!.background)
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
            grabbedToken!!.setPosition(newX, newY)
            ViewFacade.addMarker(grabbedToken!!)
            updateTokens()
        }
    }

    private fun dropToken(
        x: Int,
        y: Int
    ) { /* Places the currently grabbed token to last click's location, and drops it*/

        val newX = (x - (grabbedToken!!.hitBox.width / 2))
        val newY = (y - (grabbedToken!!.hitBox.height / 2))
        grabbedToken!!.setPosition(newX, newY)
        grabbedToken = null
        ViewFacade.removeMarker()
        updateTokens()


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
        ViewFacade.repaintFrames()
    }

    fun selectToken(
        x: Int,
        y: Int
    ) { //cheks if the point taken was on a token, if it is, transmits it to SelectPanel to display the token's characteristics
        var selected = getTokenFromXY(x, y)
        if (selected != null) {
            ViewFacade.setSelectedToken(selected)
            updateTokens()
        }
    }

    fun updateTokens() { //Updates the tokens on the maps by repainting everything //TODO: replace mapToken with activeScene.elements
        ViewFacade.placeTokensOnMaps(activeScene!!.elements)
    }

    fun toggleVisibility(token: Element?) {
        if (token != null) {
            token.isVisible = !token.isVisible
            updateTokens()
        }
    }

}