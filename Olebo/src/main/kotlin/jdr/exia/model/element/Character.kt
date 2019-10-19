package jdr.exia.model.element

import javax.swing.ImageIcon

abstract class Character(
    id: Int,
    val maxHealth: Int,
    val maxMana: Int,
    name: String,
    sprite: ImageIcon,
    position: Position,
    visible: Boolean,
    size: Size,
    var currentHealth: Int = maxHealth,
    var currentMana: Int = maxMana,
    val player: Boolean = false,
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
