package jdr.exia.model.element

import javax.swing.ImageIcon

abstract class Character(
    val maxHealth: Int,
    val maxMana: Int,
    name: String,
    sprite: ImageIcon,
    position: Position,
    visible: Boolean,
    size: Size,
    val player: Boolean = false
) : Element(
    name,
    sprite,
    position,
    visible,
    size
) {
    var currentHealth = maxHealth
    var currentMana = maxMana
}
