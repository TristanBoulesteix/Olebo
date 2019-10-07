package jdr.exia.view

import jdr.exia.model.element.Element
import java.awt.Image

interface gameFrame {

    fun updateMap(tokens: MutableList<Element>)

    fun setMapBackground(imageName: String)



}