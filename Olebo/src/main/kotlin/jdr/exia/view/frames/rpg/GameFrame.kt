package jdr.exia.view.frames.rpg

import jdr.exia.model.utils.Elements
import jdr.exia.view.frames.Reloadable

/**
 * This interface regroups all the methods that both PlayerFrame and MasterFrame need to have
 */
interface GameFrame : Reloadable {
    fun updateMap(tokens: Elements)

    fun setMapBackground(imageName: String)
}