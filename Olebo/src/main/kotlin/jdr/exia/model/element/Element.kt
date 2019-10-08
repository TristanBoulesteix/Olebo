package jdr.exia.model.element

import org.w3c.dom.css.Rect
import java.awt.Rectangle
import javax.swing.ImageIcon
// TODO("Add hitbox")
open class Element( //TODO return to abstract
    val name: String,
    val sprite: ImageIcon,
    var position: Position,
    var visible: Boolean = false,
    val size: Size,
    var hitBox: Rectangle = Rectangle(position.x,position.y,size.thousandth,size.thousandth)

){
    fun setPosition(x: Int,y: Int){
        this.position = Position(x,y)
        this.hitBox = Rectangle(x,y,size.thousandth,size.thousandth)
    }
}

