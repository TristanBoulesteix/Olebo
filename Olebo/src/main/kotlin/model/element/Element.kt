package model.element

import model.act.Scene
import model.dao.DAO
import model.dao.InstanceTable
import model.utils.isCharacter
import model.utils.toBoolean
import model.utils.toInt
import utils.CharacterException
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Rectangle
import javax.imageio.ImageIO
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
                    if (b.isCharacter()) {
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
    private val blueprint by Blueprint referencedOn InstanceTable.idBlueprint
    var scene by Scene referencedOn InstanceTable.idScene

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
        get() = transaction(DAO.database) {
            if (blueprint.type.typeElement == Type.BASIC)
                ImageIcon(ImageIO.read(Element::class.java.classLoader.getResourceAsStream("sprites/${blueprint.sprite}")))
            else
                ImageIcon(blueprint.sprite)
        }
    val name
        get() = transaction(DAO.database) { blueprint.realName }
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
                x = value.x
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