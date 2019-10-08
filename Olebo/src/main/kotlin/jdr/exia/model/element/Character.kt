package jdr.exia.model.element

import java.awt.Rectangle
import javax.swing.ImageIcon

abstract class Character(
    val maxHealth: Int,
    val maxMana: Int,
    name: String,
    sprite: ImageIcon,
    position: Position,
    visible: Boolean,
    hitbox: Rectangle,
    size: Size,
    val player: Boolean = false
) : Element(
    name,
    sprite,
    position,
    visible,
    size,
    hitbox
) {
    var currentHealth = maxHealth
    var currentMana = maxMana
}
