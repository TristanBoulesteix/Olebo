package jdr.exia.model.element

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import jdr.exia.localization.*
import jdr.exia.model.act.Scene
import jdr.exia.model.command.Command
import jdr.exia.model.command.CommandManager
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.tools.CharacterException
import jdr.exia.model.tools.isCharacter
import jdr.exia.model.type.Image
import jdr.exia.model.type.inputStreamFromString
import jdr.exia.view.tools.rotateImage
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Rectangle
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Element(id: EntityID<Int>) : Entity<Int>(id) {
    // Value stored into the database
    private val blueprint by Blueprint referencedOn InstanceTable.idBlueprint
    var scene by Scene referencedOn InstanceTable.idScene

    // Variables stored into the database
    private var visibleValue by InstanceTable.visible
    private var currentHP by InstanceTable.currentHP
    private var currentMP by InstanceTable.currentMP
    var orientation by InstanceTable.orientation

    private var x by InstanceTable.x
    private var y by InstanceTable.y
    private var sizeEntity by SizeElement.SizeEntity referencedOn InstanceTable.idSize
    private var layerEntity by Layer.LayerEntity referencedOn InstanceTable.layer

    var alias by InstanceTable.alias
    var isDeleted by InstanceTable.deleted

    // Value from the Blueprint
    val sprite by lazyRotatedSprite()

    val spriteBitmap
        get() = transaction {
            Image(blueprint.sprite).let {
                if (blueprint.type == TypeElement.Basic) {
                    useResource("sprites/${it.path}", ::loadImageBitmap)
                } else {
                    it.toBitmap()
                }
                // TODO : Add rotation
            }
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
                x.toInt(),
                y.toInt(),
                size.value,
                size.value
            )
        }

    var isVisible
        get() = transaction { visibleValue }
        private set(value) {
            transaction { visibleValue = value }
        }

    var size
        get() = transaction { sizeEntity.size }
        private set(value) {
            transaction { sizeEntity = SizeElement.SizeEntity[value] }
        }

    var priority
        get() = transaction { layerEntity.layer }
        set(value) {
            transaction { layerEntity = Layer.LayerEntity[value] }
        }

    var referenceOffset
        get() = Offset(x, y)
        private set(value) {
            transaction {
                x = value.x
                y = value.y
            }
        }

    val centerOffset
        get() = Offset(this.hitBox.centerX.toFloat(), this.hitBox.centerY.toFloat())

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
        orientation = if (orientation >= 270.0) 0f else orientation + 90f
    }

    private fun rotateLeft() = transaction {
        orientation = if (orientation <= 0.0) 270f else orientation - 90f
    }

    // --- Command functions ---

    fun cmdPosition(point: Offset, manager: CommandManager) {
        val previousPosition = this.referenceOffset

        manager += object : Command {
            override val label = StringLocale[STR_MOVE_ELEMENT]

            override fun exec() {
                this@Element.referenceOffset = point
            }

            override fun cancelExec() {
                if (stillExist())
                    this@Element.referenceOffset = previousPosition
            }
        }
    }

    /**
     * Check if the element still exists in the database
     */
    fun stillExist() = transaction { Element.findById(this@Element.id) != null }

    override fun equals(other: Any?) = other is Element && this.id == other.id

    override fun hashCode() = this.id.value

    private fun lazyRotatedSprite() = object : ReadOnlyProperty<Element, BufferedImage> {
        private fun getResourceAsStream(name: String) = Element::class.java.classLoader.getResourceAsStream(name)

        val originalImage by lazy {
            transaction {
                if (blueprint.type == TypeElement.Basic) {
                    ImageIO.read(getResourceAsStream("sprites/${blueprint.sprite}"))
                } else {
                    ImageIO.read(inputStreamFromString(blueprint.sprite))
                }
            }
        }

        var rotation: Float = 0f

        lateinit var rotatedImage: BufferedImage

        override fun getValue(thisRef: Element, property: KProperty<*>): BufferedImage {
            if (rotation != orientation || !::rotatedImage.isInitialized) {
                reloadRotatedImage()
            }

            return rotatedImage
        }

        fun reloadRotatedImage() {
            rotatedImage = originalImage.rotateImage(orientation)
            rotation = orientation
        }
    }

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

        fun cmdVisibility(visibility: Boolean, manager: CommandManager, elements: List<Element>) {
            val previousVisibilities = elements.map { it.isVisible }

            manager += object : Command {
                override val label =
                    StringLocale[if (elements.size == 1) ST_CHANGE_VISIBILITY else ST_CHANGE_VISIBILITY_PLR]

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

        fun cmdOrientationToRight(manager: CommandManager, elements: List<Element>) {
            val previousOrientation = elements.map { it.orientation }

            manager += object : Command {
                override val label = StringLocale[STR_ROTATE_TO_RIGHT]

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

        fun cmdOrientationToLeft(manager: CommandManager, elements: List<Element>) {
            val previousOrientation = elements.map { it.orientation }

            manager += object : Command {
                override val label = StringLocale[STR_ROTATE_TO_LEFT]

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

        fun cmdDimension(size: SizeElement, manager: CommandManager, elements: List<Element>) {
            val elementsFiltered = elements.filter { it.size != size }

            val previousSize = elementsFiltered.map { it.size }

            if (elementsFiltered.isNotEmpty())
                manager += object : Command {
                    override val label =
                        StringLocale[if (elements.size == 1) STR_RESIZE_ELEMENT else STR_RESIZE_ELEMENT_PLR]

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

        fun cmdPosition(elementsToPoint: Map<Element, Offset>, manager: CommandManager) {
            val previousPoints = elementsToPoint.mapValues { it.key.referenceOffset }

            manager += object : Command {
                override val label = StringLocale[STR_MOVE_ELEMENTS]

                override fun exec() = elementsToPoint.forEach { (element, newPoint) ->
                    element.referenceOffset = newPoint
                }

                override fun cancelExec() = previousPoints.forEach { (element, oldPoint) ->
                    element.referenceOffset = oldPoint
                }
            }
        }
    }
}