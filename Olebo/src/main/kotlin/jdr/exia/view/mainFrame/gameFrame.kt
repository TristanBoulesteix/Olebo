package jdr.exia.view.mainFrame

import jdr.exia.model.element.Element
import java.awt.Image

interface gameFrame { //this interface regroups all the methods that both PlayerFrame and MasterFrame need to have

    fun updateMap(tokens: MutableList<Element>)

    fun setMapBackground(imageName: String)



}