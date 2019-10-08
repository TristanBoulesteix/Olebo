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
    var selectedToken: Element? = null
    init {
        ViewManager.setMapBackground("/tools.jpg")
       var toky = Element("test",ImageIcon(ImageIO.read(Element::class.java.getResource("/AH!.png").openStream())),Position(500,500),
           true,Size.M)
        this.addToken(toky)
        this.updateTokens()

    }

    fun loadTokenFromClick(x: Int,y: Int){ //Takes a point that was clicked, and return the 1st token that connects
        val clickedPoint = Point(x,y)

        for(token in mapTokens){
            if (token.hitBox.contains(clickedPoint)){
                selectedToken = token
                updateTokens()
            }
        }

    }

    fun dragDrop(x:Int,y: Int){

        if (selectedToken == null){
            loadTokenFromClick(x,y)
        }
        else {
            dropToken(x,y)
        }
    }

    fun dropToken(x: Int,y: Int){
       selectedToken!!.setPosition(x,y)
        selectedToken = null
       updateTokens()

    }

    fun addToken(token: Element){
        this.mapTokens.add(token)
    }

    fun updateTokens (){ //updates the tokens on the maps
        ViewManager.placeTokensOnMaps(mapTokens)
    }

}