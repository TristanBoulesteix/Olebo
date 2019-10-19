package jdr.exia.model.element

import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.utils.elementListOf
import jdr.exia.model.utils.getEnum
import org.jetbrains.exposed.sql.ResultRow
import java.awt.Rectangle
import javax.swing.ImageIcon

abstract class Element(
    val idInstance: Int,
    val name: String,
    val sprite: ImageIcon,
    var position: Position,
    var visible: Boolean = false,
    size: Size,
    val idBlueprint: Int,
    val idScene: Int
) {
    companion object ElementFactory {
        fun buildElementFromRequest(result: MutableList<ResultRow>, scene: Int): MutableList<Element> {
            val elements = elementListOf(scene)

            result.forEach {
                elements += with(Blueprint[it[InstanceTable.idBlueprint]]) {
                    when (this.type.typeElement) {
                        Type.OBJECT -> Item(
                            it[InstanceTable.id].value,
                            this.name,
                            ImageIcon(this.sprite),
                            Position(it[InstanceTable.x], it[InstanceTable.x]),
                            it[InstanceTable.visible].getEnum(),
                            Size.valueOf(it[InstanceTable.size]),
                            this.id.value,
                            scene
                        )
                        Type.PNJ -> NonPlayableCharacter(
                            it[InstanceTable.id].value,
                            this.HP,
                            this.MP,
                            this.name,
                            ImageIcon(this.sprite),
                            Position(it[InstanceTable.x], it[InstanceTable.x]),
                            it[InstanceTable.visible].getEnum(),
                            Size.valueOf(it[InstanceTable.size]),
                            it[InstanceTable.currentHP],
                            it[InstanceTable.currentMP],
                            this.id.value,
                            scene
                        )
                        Type.PJ -> PlayableCharacter(
                            it[InstanceTable.id].value,
                            this.HP,
                            this.MP,
                            this.name,
                            ImageIcon(this.sprite),
                            Position(it[InstanceTable.x], it[InstanceTable.x]),
                            it[InstanceTable.visible].getEnum(),
                            Size.valueOf(it[InstanceTable.size]),
                            it[InstanceTable.currentHP],
                            it[InstanceTable.currentMP],
                            this.id.value,
                            scene
                        )
                    }
                }
            }

            return elements
        }
    }

    var hitBox = Rectangle(position.x, position.y, size.absoluteSizeValue, size.absoluteSizeValue)

    var size = size
        set(newSize) {
            field = newSize
            hitBox = Rectangle(position.x, position.y, newSize.absoluteSizeValue, newSize.absoluteSizeValue)
        }

    fun setPosition(x: Int, y: Int) {
        this.position = Position(x, y)
        this.hitBox = Rectangle(x, y, size.absoluteSizeValue, size.absoluteSizeValue)
    }
}

