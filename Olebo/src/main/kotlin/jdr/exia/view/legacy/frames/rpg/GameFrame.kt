package jdr.exia.view.legacy.frames.rpg

import jdr.exia.model.element.Elements
import jdr.exia.view.legacy.frames.Reloadable
import java.awt.Image

/**
 * This interface regroups all the methods that both PlayerFrame and MasterFrame need to have
 */
interface GameFrame : Reloadable {
    fun updateMap(tokens: Elements)

    var mapBackground: Image?
}