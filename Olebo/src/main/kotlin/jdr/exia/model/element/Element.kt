package jdr.exia.model.element

import javax.swing.ImageIcon
// TODO("Add hitbox")
abstract class Element(
    val name: String,
    val sprite: ImageIcon,
    var position: Position,
    var visible: Boolean = false,
    val size: Size
)