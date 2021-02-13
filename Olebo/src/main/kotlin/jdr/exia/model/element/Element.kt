package jdr.exia.model.element

import jdr.exia.localization.*
import jdr.exia.model.act.Scene
import jdr.exia.model.command.Command
import jdr.exia.model.command.CommandManager
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.utils.Elements
import jdr.exia.model.utils.Point
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
            return transaction {
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

                override fun cancelExec() = transaction {
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

                override fun cancelExec() = transaction {
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

        fun cmdPosition(points: List<Point>, manager: CommandManager, elements: Elements) {
            assert(points.size == elements.size) { "Error: Positions does not match elements number." }

            val previousPosition = elements.map { it.referencePoint }

            manager += object : Command() {
                override val label by StringDelegate(STR_MOVE_ELEMENTS)

                override fun exec() = elements.forEachIndexed { index, element ->
                    element.referencePoint = points[index]
                }

                override fun cancelExec() = elements.forEachIndexed { index, element ->
                    if (element.stillExist())
                        element.referencePoint = previousPosition[index]
                }
            }
        }

        fun cmdDelete(manager: CommandManager, elements: Elements) {
            manager += object : Command() {
                override val label by StringDelegate(STR_DELETE_SELECTED_TOKENS)

                override fun exec() = transaction {
                    elements.forEach {
                        it.isDeleted = true
                    }
                }

                override fun cancelExec() = transaction {
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
    val sprite
        get() = transaction {
            if (blueprint.type.typeElement == Type.BASIC) {
                ImageIcon(ImageIO.read(Element::class.java.classLoader.getResourceAsStream("sprites/${blueprint.sprite}")))
            } else {
                ImageIcon(blueprint.sprite)
            }.rotate(orientation)
        }

    val name
        get() = transaction { blueprint.realName }

    val maxHP
        get() = transaction { blueprint.HP }

    val maxMana
        get() = transaction { blueprint.MP }

    val type
        get() = transaction { blueprint.type }

    // Custom getters / setters / variables / values
    val hitBox
        get() = transaction {
            Rectangle(
                x,
                y,
                sizeElement.absoluteSizeValue,
                sizeElement.absoluteSizeValue
            )
        }

    var isVisible
        get() = transaction { visibleValue }
        private set(value) {
            transaction { visibleValue = value }
        }

    var size
        get() = transaction { sizeElement.sizeElement }
        private set(value) {
            transaction { sizeElement = value.size }
        }

    var priority
        get() = transaction { priorityElement.priorityElement }
        set(value) {
            transaction { priorityElement = value.priority }
        }

    var referencePoint
        get() = Point(x, y)
        private set(value) {
            transaction {
                x = value.x
                y = value.y
            }
        }

    val centerPoint
        get() = Point(this.hitBox.centerX.toInt(), this.hitBox.centerY.toInt())

    var currentHealth
        get() = if (this.isCharacter()) currentHP!! else throw Exception("Cet élément n'est pas un personnage !")
        set(value) = if (this.isCharacter()) currentHP = value
        else throw CharacterException(this::class, "currentHealth")

    var currentMana
        get() = if (this.isCharacter()) currentMP!! else throw Exception("Cet élément n'est pas un personnage !")
        set(value) = if (this.isCharacter()) currentMP = value
        else throw CharacterException(this::class, "currentMana")

    // --- General functions ---
    private fun rotateRight() = transaction {
        orientation = if (orientation >= 270.0) 0.0 else orientation + 90.0
    }

    private fun rotateLeft() = transaction {
        orientation = if (orientation <= 0.0) 270.0 else orientation - 90.0
    }

    // --- Command functions ---

    fun cmdPosition(point: Point, manager: CommandManager) {
        val previousPosition = this.referencePoint

        manager += object : Command() {
            override val label by StringDelegate(STR_MOVE_ELEMENT)

            override fun exec() {
                this@Element.referencePoint = point
            }

            override fun cancelExec() {
                if (stillExist())
                    this@Element.referencePoint = previousPosition
            }
        }
    }

    /**
     * Check if the element still exists in the database
     */
    fun stillExist() = transaction { Element.findById(this@Element.id) != null }
}