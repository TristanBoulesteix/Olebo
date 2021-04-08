package jdr.exia.view.frames.rpg

import jdr.exia.model.element.Elements
import jdr.exia.view.frames.Reloadable
import java.awt.Image

/**
 * This interface regroups all the methods that both PlayerFrame and MasterFrame need to have
 */
interface GameFrame : Reloadable {
    fun updateMap(tokens: Elements)

    var mapBackground: Image?
}