package jdr.exia.model.element

import jdr.exia.CharacterException
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.utils.isCharacter
import jdr.exia.model.utils.toBoolean
import jdr.exia.model.utils.toInt
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import java.awt.Rectangle
import javax.swing.ImageIcon

class Element(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Element>(InstanceTable)

    // Value stored into the database
    private val blueprint by Blueprint referencedOn InstanceTable.idBlueprint
    val idScene by InstanceTable.idScene

    // Variables stored into the database
    private var visible by InstanceTable.visible
    private var currentHP by InstanceTable.currentHP
    private var currentPM by InstanceTable.currentMP
    var x by InstanceTable.x
    var y by InstanceTable.y
    var sizeElement by Size.SizeElement referencedOn InstanceTable.idSize


    // Value from the Blueprint
    val sprite
        get() = ImageIcon(blueprint.sprite)
    val name
        get() = blueprint.name
    val maxHP
        get() = blueprint.HP
    val maxMana
        get() = blueprint.MP
    val type
        get() = blueprint.type

    // Custom getters / setters / variables / values
    val hitBox
        get() = transaction { Rectangle(x, y, sizeElement.absoluteSizeValue, sizeElement.absoluteSizeValue) }

    var isVisible
        get() = visible.toBoolean()
        set(value) {
            visible = value.toInt()
        }

    var size
        get() = sizeElement.sizeElement
        set(value) {
            sizeElement = value.size
        }

    var position
        get() = Position(x, y)
        set(value) {
            this.x = value.x
            this.y = value.y
        }

    var currentHealth
        get() = if (this.isCharacter()) currentHP!! else throw Exception("Cet élément n'est pas un personnage !")
        set(value) = if (this.isCharacter()) currentHP = value
        else throw CharacterException(this::class, "currentHealth")

    var currentMana
        get() = if (this.isCharacter()) currentPM!! else throw Exception("Cet élément n'est pas un personnage !")
        set(value) = if (this.isCharacter()) currentPM = value
        else throw CharacterException(this::class, "currentMana")
}