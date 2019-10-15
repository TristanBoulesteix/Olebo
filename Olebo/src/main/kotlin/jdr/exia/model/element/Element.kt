package jdr.exia.model.element

import java.awt.Rectangle
import javax.swing.ImageIcon

// TODO("Add hitbox")
abstract class Element( //TODO return to abstract
    val name: String,
    val sprite: ImageIcon,
    var position: Position,
    var visible: Boolean = false,
    size: Size
) {
    var hitBox: Rectangle = Rectangle(position.x, position.y, size.absoluteSizeValue, size.absoluteSizeValue)

    var size: Size = size
        set(newSize: Size) {
            field = newSize
            hitBox = Rectangle(position.x, position.y, newSize.absoluteSizeValue, newSize.absoluteSizeValue)
        }

    fun setPosition(x: Int, y: Int) {
        this.position = Position(x, y)
        this.hitBox = Rectangle(x, y, size.absoluteSizeValue, size.absoluteSizeValue)
    }
}

