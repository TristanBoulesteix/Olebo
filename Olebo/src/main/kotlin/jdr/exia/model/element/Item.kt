package jdr.exia.model.element

import javax.swing.ImageIcon

class Item(
    id: Int,
    name: String,
    sprite: ImageIcon,
    position: Position,
    visible: Boolean,
    size: Size,
    idBlueprint: Int,
    idScene: Int
) : Element(
    id,
    name,
    sprite,
    position,
    visible,
    size,
    idBlueprint,
    idScene
)
