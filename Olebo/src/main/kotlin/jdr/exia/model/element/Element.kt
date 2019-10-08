package jdr.exia.model.element

import java.awt.Rectangle
import javax.swing.ImageIcon
// TODO("Add hitbox")
 abstract class Element( //TODO return to abstract
    val name: String,
    val sprite: ImageIcon,
    var position: Position,
    var visible: Boolean = false,
    var hitBox: Rectangle = Rectangle(position.x,position.y,50,50),
    val size: Size
)
