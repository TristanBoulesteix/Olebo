package jdr.exia.model.element

import javax.swing.ImageIcon

class NonPlayableCharacter(
    maxHealth: Int,
    maxMana: Int,
    name: String,
    sprite: ImageIcon,
    position: Position,
    visible: Boolean,
    size: Size
) : Character(maxHealth, maxMana, name, sprite, position, visible, size, false)
