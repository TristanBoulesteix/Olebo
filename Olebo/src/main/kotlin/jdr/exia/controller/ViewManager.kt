package jdr.exia.controller

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.element.Element
import jdr.exia.model.element.PlayableCharacter
import jdr.exia.model.element.Position
import jdr.exia.model.element.Size
import jdr.exia.view.mainFrame.ViewFacade
import javax.imageio.ImageIO
import javax.swing.ImageIcon

object ViewManager {

    fun testRun(){ //TODO: Remove ASAP
        ViewFacade.setMapBackground("/tools.jpg")
        val talkien = PlayableCharacter(
            25,
            15,
            "gandalf",
            ImageIcon(ImageIO.read(PlayableCharacter::class.java.getResource("/purse.jpg").openStream())),
            Position(250,25),
            true,
            Size.M
        )
        addToken(talkien)
        updateTokens()
        ViewFacade.testRun()
    }



    private var activeAct: Act? = null
    private var activeScene: Scene? = null
    var mapTokens = mutableListOf<Element>() //TODO: replace mapToken with activeScene.elements
    var grabbedToken: Element? = null
    init {}

    fun clickNDrop(x:Int,y: Int){
        /*If a token has already been grabbed, then it is placed with dropToken(),
        else tries to find a token where the screen was clicked with  loadTokenFromClick(x,y)*/
        if (grabbedToken == null){
           grabbedToken = getTokenFromXY(x,y)
            if(grabbedToken!=null) {
                ViewFacade.addMarker(grabbedToken!!)
                updateTokens()
            }

        }
        else {
            dropToken(x,y)
        }
    }

    fun initializeAct(act:Act) {
        activeAct = act
        loadCurrentScene()
    }

    fun removeToken(token: Element){ //removes given token from MutableList
        mapTokens.remove(token) //TODO: replace mapToken with activeScene.elements
    }

    fun changeCurrentScene(sceneId: Int){
        activeAct!!.sceneId = sceneId
        loadCurrentScene()
        updateTokens()
    }

    private fun loadCurrentScene(){
        with(activeAct) {
           activeScene = this!!.scenes.findWithId(activeAct!!.sceneId)
           ViewFacade.setMapBackground(activeScene!!.background)
            ViewFacade.turnVisible()
        }
    }

    fun moveToken(x:Int,y:Int){ //Changes a token's position without dropping it (a moved token stays selected) , intended for small steps
        if(grabbedToken != null) {
            val newX = (x - (grabbedToken!!.hitBox.width / 2))
            val newY = (y - (grabbedToken!!.hitBox.height / 2))
            grabbedToken!!.setPosition(newX, newY)
            ViewFacade.addMarker(grabbedToken!!)
            updateTokens()
        }
    }

    private fun dropToken(x: Int,y: Int){ /* Places the currently grabbed token to last click's location, and drops it*/

            val newX = (x - (grabbedToken!!.hitBox.width / 2))
            val newY = (y - (grabbedToken!!.hitBox.height / 2))
            grabbedToken!!.setPosition(newX, newY)
            grabbedToken = null
            ViewFacade.removeMarker()
            updateTokens()


    }

    private fun addToken(token: Element){ //Adds a single token to this object's Token list
        this.mapTokens.add(token)
    }

    private fun getTokenFromXY(x: Int, y: Int): Element?{ //Receives a clicked point (x,y), returns the first soken found in the Tokens array, or null if none matched
        for(token in mapTokens){ //TODO: replace mapToken with activeScene.elements
            if (token.hitBox.contains(x,y)){
                return(token)
            }
        }
        return null
    }



    fun repaint(){
        ViewFacade.repaintFrames()
    }

    fun selectToken(x: Int,y:Int){ //cheks if the point taken was on a token, if it is, transmits it to SelectPanel to display the token's characteristics
        var selected = getTokenFromXY(x,y)
        if(selected!=null){
            ViewFacade.setSelectedToken(selected)
            updateTokens()
        }
    }

    fun updateTokens (){ //Updates the tokens on the maps by repainting everything //TODO: replace mapToken with activeScene.elements
        ViewFacade.placeTokensOnMaps(mapTokens)
    }

    fun toggleVisibility(token: Element?) {
        if (token != null) {
            token.visible = !token.visible
            updateTokens()
        }
    }

}