package view.frames.rpg

import model.utils.Elements
import view.frames.Reloadable

/**
 * This interface regroups all the methods that both PlayerFrame and MasterFrame need to have
 */
interface GameFrame : Reloadable {
    fun updateMap(tokens: Elements)

    fun setMapBackground(imageName: String)
}