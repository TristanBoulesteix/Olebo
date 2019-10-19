package jdr.exia.model.element

import javax.swing.ImageIcon

class NonPlayableCharacter(
    id: Int,
    maxHealth: Int,
    maxMana: Int,
    name: String,
    sprite: ImageIcon,
    position: Position,
    visible: Boolean,
    size: Size,
    idBlueprint: Int,
    idScene: Int,
    currentHealth: Int = maxHealth,
    currentMana: Int = maxMana
) : Character(
    id,
    maxHealth,
    maxMana,
    name,
    sprite,
    position,
    visible,
    size,
    currentHealth,
    currentMana,
    false,
    idBlueprint,
    idScene
)
