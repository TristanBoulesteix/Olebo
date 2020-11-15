package view.frames.rpg

import model.utils.Elements

interface GameFrame { //this interface regroups all the methods that both PlayerFrame and MasterFrame need to have

    fun updateMap(tokens: Elements)

    fun setMapBackground(imageName: String)



}