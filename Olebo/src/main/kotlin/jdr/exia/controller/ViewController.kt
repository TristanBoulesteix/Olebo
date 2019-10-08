package jdr.exia.controller
import jdr.exia.view.*

import jdr.exia.model.element.Element
import javax.imageio.ImageIO
import javax.swing.ImageIcon

object ViewController {

    var mapTokens = mutableListOf<Element>()

    init {
        ViewManager.setMapBackground("/tools.jpg")
      /*  var toky = Element(ImageIcon(ImageIO.read(Element::class.java.getResource("/AH!.png").openStream())),500,500)
        this.addToken(toky)
        this.updateTokens()*/

    }

    fun addToken(token: Element){
        this.mapTokens.add(token)
    }

    fun updateTokens (){ //updates the tokens on the maps
        ViewManager.placeTokensOnMaps(mapTokens)
    }

}