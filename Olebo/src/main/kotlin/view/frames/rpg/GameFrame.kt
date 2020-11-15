package view.frames.rpg

import model.utils.Elements

/**
 * This interface regroups all the methods that both PlayerFrame and MasterFrame need to have
 */
interface GameFrame {
    fun updateMap(tokens: Elements)

    fun setMapBackground(imageName: String)

    fun reload()
}