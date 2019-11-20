package jdr.exia.model.element

import jdr.exia.CharacterException
import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.utils.isCharacter
import jdr.exia.model.utils.toBoolean
import jdr.exia.model.utils.toInt
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Rectangle
import javax.swing.ImageIcon

class Element(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, Element>(InstanceTable) {
        /**
         * Create a new element with a given blueprint
         *
         * @param b A given Blueprint
         *
         * @return The newly created element
         */
        fun createElement(b: Blueprint): Element {
            return transaction(DAO.database) {
                val id = InstanceTable.insertAndGetId {
                    if (b.type.typeElement != Type.OBJECT) {
                        it[currentHP] = b.HP
                        it[currentMP] = b.MP
                    }
                    it[idBlueprint] = b.id.value
                }

                Element[id]
            }
        }
    }

    // Value stored into the database
    private var blueprint by Blueprint referencedOn InstanceTable.idBlueprint

    // Variables stored into the database
    private var visible by InstanceTable.visible
    private var currentHP by InstanceTable.currentHP
    private var currentMP by InstanceTable.currentMP
    var x by InstanceTable.x
    var y by InstanceTable.y
    var sizeElement by Size.SizeElement referencedOn InstanceTable.idSize
    var orientation by InstanceTable.orientation

    // Value from the Blueprint
    val sprite
        get() = transaction(DAO.database) { ImageIcon(blueprint.sprite) }
    val name
        get() = transaction(DAO.database) { blueprint.name }
    val maxHP
        get() = transaction(DAO.database) { blueprint.HP }
    val maxMana
        get() = transaction(DAO.database) { blueprint.MP }
    val type
        get() = transaction(DAO.database) { blueprint.type }

    // Custom getters / setters / variables / values
    val hitBox
        get() = transaction(DAO.database) {
            Rectangle(
                x,
                y,
                sizeElement.absoluteSizeValue,
                sizeElement.absoluteSizeValue
            )
        }

    var isVisible
        get() = transaction(DAO.database) { visible.toBoolean() }
        set(value) {
            transaction(DAO.database) { visible = value.toInt() }
        }

    var size
        get() = transaction(DAO.database) { sizeElement.sizeElement }
        set(value) {
            transaction(DAO.database) { sizeElement = value.size }
        }

    var position
        get() = Position(x, y)
        set(value) {
            transaction(DAO.database) {
                x = value.x;
                y = value.y
            }
        }

    var currentHealth
        get() = if (this.isCharacter()) currentHP!! else throw Exception("Cet élément n'est pas un personnage !")
        set(value) = if (this.isCharacter()) currentHP = value
        else throw CharacterException(this::class, "currentHealth")

    var currentMana
        get() = if (this.isCharacter()) currentMP!! else throw Exception("Cet élément n'est pas un personnage !")
        set(value) = if (this.isCharacter()) currentMP = value
        else throw CharacterException(this::class, "currentMana")
}