package jdr.exia.controller

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.element.Element
import jdr.exia.model.element.Position
import jdr.exia.model.element.Size
import jdr.exia.view.mainFrame.ViewFacade
import java.awt.Point
import javax.imageio.ImageIO
import javax.swing.ImageIcon

object ViewManager {

    fun testRun(){ //TODO: Remove ASAP

        ViewFacade.setMapBackground("/tools.jpg")

        var toky = Element(
            "test",
            ImageIcon(ImageIO.read(Element::class.java.getResource("/AH!.png").openStream())),
            Position(500,500),
            true,
            Size.S)
        var tokar = Element(
            "test",
            ImageIcon(ImageIO.read(Element::class.java.getResource("/blue.png").openStream())),
            Position(550,500),
            false,
            Size.XXL)

        addToken(toky)
        addToken(tokar)
        updateTokens()
        ViewFacade.testRun()
    }

    var mapTokens = mutableListOf<Element>()
    var grabbedToken: Element? = null
    var activeAct: Act? = null
    var activeScene: Scene? = null
    init {


        testRun()


    }



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

    fun changeCurrentScene(sceneId: Int){
        activeAct!!.sceneId = sceneId
    }

    private fun loadCurrentScene(){
        with(activeAct) {
           activeScene = this!!.scenes.findWithId(activeAct!!.sceneId)
           mapTokens//TODO : load tokens from instances table
           ViewFacade.setMapBackground(activeScene!!.background)
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
        for(token in mapTokens){
            if (token.hitBox.contains(x,y)){

                return(token)

            }
        }
        return null
    }

    fun selectToken(x: Int,y:Int){ //cheks if the point taken was on a token, if it is, transmits it to SelectPanel to display the token's characteristics
        var selected = getTokenFromXY(x,y)

        if(selected!=null){

            ViewFacade.setSelectedToken(selected)
            updateTokens()
        }
    }

    private fun updateTokens (){ //Updates the tokens on the maps by repainting everything
        ViewFacade.placeTokensOnMaps(mapTokens)
    }

}