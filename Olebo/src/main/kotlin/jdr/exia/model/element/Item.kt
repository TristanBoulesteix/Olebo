package jdr.exia.model.element

import java.awt.Rectangle
import javax.swing.ImageIcon

class Item(name: String, sprite: ImageIcon, position: Position, visible: Boolean, hitbox: Rectangle, size: Size) : Element(
    name,
    sprite,
    position,
    visible,
    size,
    hitbox
)
