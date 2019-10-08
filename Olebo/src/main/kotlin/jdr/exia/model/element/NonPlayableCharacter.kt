package jdr.exia.model.element

import java.awt.Rectangle
import javax.swing.ImageIcon

class NonPlayableCharacter(
    maxHealth: Int,
    maxMana: Int,
    name: String,
    sprite: ImageIcon,
    position: Position,
    visible: Boolean,
    hitbox: Rectangle,
    size: Size
) : Character(maxHealth, maxMana, name, sprite, position, visible, hitbox, size, false)
