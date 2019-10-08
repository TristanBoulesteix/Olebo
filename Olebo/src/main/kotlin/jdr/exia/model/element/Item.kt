package jdr.exia.model.element

import javax.swing.ImageIcon

class Item(name: String, sprite: ImageIcon, position: Position, visible: Boolean, size: Size) : Element(
    name,
    sprite,
    position,
    visible,
    size
)
