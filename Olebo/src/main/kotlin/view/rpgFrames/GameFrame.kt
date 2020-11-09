package view.rpgFrames

import model.element.Element

interface GameFrame { //this interface regroups all the methods that both PlayerFrame and MasterFrame need to have

    fun updateMap(tokens: MutableList<Element>)

    fun setMapBackground(imageName: String)



}