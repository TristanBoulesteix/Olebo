package jdr.exia.model.act

import jdr.exia.model.element.Element
import jdr.exia.model.element.Position
import javax.swing.ImageIcon

class Scene(val name: String, val background: ImageIcon, val elements: MutableList<Element>, var spawnPoint: Position = Position(0, 0))
