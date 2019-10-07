package jdr.exia.controller
import jdr.exia.view.*

import jdr.exia.model.element.Element

object ViewController {

    var mapTokens = mutableListOf<Element>()

    init {
        ViewManager.setMapBackground("/tools.jpg")

    }

    fun updateTokens (){ //updates the tokens on the maps
        ViewManager.placeTokensOnMaps(mapTokens)
    }

}