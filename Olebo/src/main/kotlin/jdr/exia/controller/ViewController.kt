package jdr.exia.controller

import jdr.exia.model.element.Element
import jdr.exia.model.element.Position
import jdr.exia.model.element.Size
import jdr.exia.view.mainFrame.ViewManager
import java.awt.Point
import java.awt.Rectangle
import javax.imageio.ImageIO
import javax.swing.ImageIcon

object ViewController {

    var mapTokens = mutableListOf<Element>()
    var grabbedToken: Element? = null
    init {
        ViewManager.setMapBackground("/tools.jpg")


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
            true,
            Size.XXL)


        addToken(toky)
        addToken(tokar)
        updateTokens()

    }



    fun clickNDrop(x:Int,y: Int){
        /*If a token has already been grabbed, then it is placed with dropToken(),
        else tries to find a token where the screen was clicked with  loadTokenFromClick(x,y)*/
        if (grabbedToken == null){
            loadTokenFromClick(x,y)
        }
        else {
            dropToken(x,y)
        }
    }
    fun loadTokenFromClick(x: Int,y: Int){ //Takes a point that was clicked, and return the 1st token that connects
        val clickedPoint = Point(x,y)

        for(token in mapTokens){
            if (token.hitBox.contains(clickedPoint)){
                grabbedToken = token
                ViewManager.addMarker(token)
                updateTokens()
            }
        }

    }


    fun moveToken(x:Int,y:Int){ //Changes a token's position without dropping it (a moved token stays selected) , intended for small steps
        if(grabbedToken != null) {
            val newX = (x - (grabbedToken!!.hitBox.width / 2))
            val newY = (y - (grabbedToken!!.hitBox.height / 2))
            grabbedToken!!.setPosition(newX, newY)
            ViewManager.addMarker(grabbedToken!!)
            updateTokens()
        }
    }

    fun dropToken(x: Int,y: Int){ /* Places the currently grabbed token to last click's location, and drops it*/

            val newX = (x - (grabbedToken!!.hitBox.width / 2))
            val newY = (y - (grabbedToken!!.hitBox.height / 2))
            grabbedToken!!.setPosition(newX, newY)
            grabbedToken = null
            ViewManager.removeMarker()
            updateTokens()


    }

    fun addToken(token: Element){ //Adds a single token to this object's Token list
        this.mapTokens.add(token)
    }

    fun updateTokens (){ //Updates the tokens on the maps
        ViewManager.placeTokensOnMaps(mapTokens)
    }

}