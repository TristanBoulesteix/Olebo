package jdr.exia.model.element

import javax.swing.ImageIcon

class PlayableCharacter(
    maxHealth: Int,
    maxMana: Int,
    name: String,
    sprite: ImageIcon,
    position: Position,
    visible: Boolean,
    size: Size,
    currentHealth: Int = maxHealth,
    currentMana: Int = maxMana
) : Character(maxHealth, maxMana, name, sprite, position, visible, size, currentHealth, currentMana, true)
