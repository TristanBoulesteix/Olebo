package jdr.exia.model.element

import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.utils.toBoolean
import jdr.exia.model.utils.toInt
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import java.awt.Rectangle
import javax.swing.ImageIcon

 abstract class Element(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Element>(InstanceTable)

    private var size by Size.SizeElement referencedOn InstanceTable.size
    private var visible by InstanceTable.visible
    var currentHealth by InstanceTable.currentHP
    var currentMana by InstanceTable.currentMP
    var x by InstanceTable.x
    var y by InstanceTable.y

    private val blueprint by Blueprint referencedOn InstanceTable.idBlueprint
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

    var hitBox = Rectangle(x, y, size.absoluteSizeValue, size.absoluteSizeValue)

    var isVisible
        get() = visible.toBoolean()
        set(value) {
            visible = value.toInt()
        }

    fun setSize(newSize: Size) {
        this.size = newSize.size
        hitBox = Rectangle(x, y, size.absoluteSizeValue, size.absoluteSizeValue)
    }

    fun getSize(): Size {
        return size.sizeElement
    }

    fun setPosition(x: Int, y: Int) {
        this.x = x
        this.y = y
        this.hitBox = Rectangle(x, y, size.absoluteSizeValue, size.absoluteSizeValue)
    }

    fun getPosition() = Position(x, y)
}

fun Element?.isCharacter(): Boolean {
    return this != null && (this.type.typeElement == Type.PNJ || this.type.typeElement == Type.PJ)
}

