package jdr.exia.model.element

import jdr.exia.localization.*
import jdr.exia.model.act.Scene
import jdr.exia.model.command.Command
import jdr.exia.model.command.CommandManager
import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.utils.Elements
import jdr.exia.model.utils.isCharacter
import jdr.exia.model.utils.rotate
import jdr.exia.utils.CharacterException
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Rectangle
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import kotlin.properties.ReadOnlyProperty

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
                    it[visible] = Settings.defaultElementVisibility
                }

                Element[id]
            }
        }

        // --- Command functions ---

        fun cmdVisiblity(visibility: Boolean, manager: CommandManager, elements: Elements) {
            val previousVisibilities = elements.map { it.isVisible }

            manager += object : Command() {
                override val label by StringDelegate(if (elements.size == 1) ST_CHANGE_VISIBILITY else ST_CHANGE_VISIBILITY_PLR)

                override fun exec() {
                    elements.forEach {
                        if (it.stillExist())
                            it.isVisible = visibility
                    }
                }

                override fun cancelExec() {
                    elements.forEachIndexed { index, element ->
                        if (element.stillExist())
                            element.isVisible = previousVisibilities[index]
                    }
                }
            }
        }

        fun cmdOrientationToRight(manager: CommandManager, elements: Elements) {
            val previousOrientation = elements.map { it.orientation }

            manager += object : Command() {
                override val label by StringDelegate(STR_ROTATE_TO_RIGHT)

                override fun exec() {
                    elements.forEach {
                        if (it.stillExist())
                            it.rotateRight()
                    }
                }

                override fun cancelExec() = transaction(DAO.database) {
                    elements.forEachIndexed { index, element ->
                        if (element.stillExist())
                            element.refresh()
                        element.orientation = previousOrientation[index]
                    }
                }
            }
        }

        fun cmdOrientationToLeft(manager: CommandManager, elements: Elements) {
            val previousOrientation = elements.map { it.orientation }

            manager += object : Command() {
                override val label by StringDelegate(STR_ROTATE_TO_LEFT)

                override fun exec() = elements.forEach {
                    if (it.stillExist())
                        it.rotateLeft()
                }

                override fun cancelExec() = transaction(DAO.database) {
                    elements.forEachIndexed { index, element ->
                        if (element.stillExist())
                            element.orientation = previousOrientation[index]
                    }
                }
            }
        }

        fun cmdDimension(size: Size, manager: CommandManager, elements: Elements) {
            val elementsFiltered = elements.filter { it.size != size }

            val previousSize = elementsFiltered.map { it.size }

            if (elementsFiltered.isNotEmpty())
                manager += object : Command() {
                    override val label by StringDelegate(if (elements.size == 1) STR_RESIZE_ELEMENT else STR_RESIZE_ELEMENT_PLR)

                    override fun exec() = elementsFiltered.forEach {
                        if (it.stillExist())
                            it.size = size
                    }

                    override fun cancelExec() = elementsFiltered.forEachIndexed { index, element ->
                        if (element.stillExist())
                            element.size = previousSize[index]
                    }
                }
        }

        fun cmdDelete(manager: CommandManager, elements: Elements) {
            manager += object : Command() {
                override val label by StringDelegate(STR_DELETE_SELECTED_TOKENS)

                override fun exec() = transaction(DAO.database) {
                    elements.forEach {
                        it.isDeleted = true
                    }
                }

                override fun cancelExec() = transaction(DAO.database) {
                    elements.forEach {
                        it.isDeleted = false
                    }
                }
            }
        }
    }

    // Value stored into the database
    private val blueprint by Blueprint referencedOn InstanceTable.idBlueprint
    var scene by Scene referencedOn InstanceTable.idScene

    // Variables stored into the database
    private var visibleValue by InstanceTable.visible
    private var currentHP by InstanceTable.currentHP
    private var currentMP by InstanceTable.currentMP
    private var orientation by InstanceTable.orientation

    private var x by InstanceTable.x
    private var y by InstanceTable.y
    private var sizeElement by Size.SizeElement referencedOn InstanceTable.idSize
    private var priorityElement by Priority.PriorityElement referencedOn InstanceTable.priority

    var alias by InstanceTable.alias
    var isDeleted by InstanceTable.deleted

    // Value from the Blueprint
    val sprite by transaction {
            if (blueprint.type.typeElement == Type.BASIC) {
                ImageIcon(ImageIO.read(Element::class.java.classLoader.getResourceAsStream("sprites/${blueprint.sprite}")))
            }
            else {
                ImageIcon(blueprint.sprite)
            }.rotate(orientation)
        }

    val name by transaction { blueprint.realName }

    val maxHP by transaction { blueprint.HP }

    val maxMana by transaction { blueprint.MP }

    val type by transaction { blueprint.type }

    // Custom getters / setters / variables / values
    val hitBox by transaction {
            Rectangle(
                x,
                y,
                sizeElement.absoluteSizeValue,
                sizeElement.absoluteSizeValue
            )
        }

    var isVisible
        get() = transaction(DAO.database) { visibleValue }
        private set(value) {
            transaction(DAO.database) { visibleValue = value }
        }

    var size
        get() = transaction(DAO.database) { sizeElement.sizeElement }
        private set(value) {
            transaction(DAO.database) { sizeElement = value.size }
        }

    var priority
        get() = transaction(DAO.database) { priorityElement.priorityElement }
        set(value) {
            transaction(DAO.database) { priorityElement = value.priority }
        }

    var position
        get() = Position(x, y)
        private set(value) {
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

    // --- General functions ---
    private fun rotateRight() = transaction(DAO.database) {
        orientation = if (orientation >= 270.0) 0.0 else orientation + 90.0
    }

    private fun rotateLeft() = transaction(DAO.database) {
        orientation = if (orientation <= 0.0) 270.0 else orientation - 90.0
    }

    // --- Command functions ---

    fun cmdPosition(position: Position, manager: CommandManager) {
        val previousPosition = this.position

        manager += object : Command() {
            override val label by StringDelegate(STR_MOVE_ELEMENT)

            override fun exec() {
                this@Element.position = position
            }

            override fun cancelExec() {
                if (stillExist())
                    this@Element.position = previousPosition
            }
        }
    }

    fun stillExist() = transaction(DAO.database) { Element.findById(this@Element.id) != null }

    /**
     * Set a value using a transaction with the default database
     */
    private fun <T> transaction(get: () -> T): ReadOnlyProperty<Element, T> =
        ReadOnlyProperty { _, _ -> transaction(DAO.database) { get() } }
}