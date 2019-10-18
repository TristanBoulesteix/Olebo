package jdr.exia.model.utils

import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.element.*
import org.jetbrains.exposed.sql.ResultRow
import javax.swing.ImageIcon

fun buildElementFromRequest(result: MutableList<ResultRow>): MutableList<Element> {
    val elements = mutableListOf<Element>()

    result.forEach {
        elements += with(Blueprint[it[InstanceTable.idBlueprint]]) {
            when (this.type.typeElement) {
                Type.OBJECT -> Item(
                    this.name,
                    ImageIcon(this.sprite),
                    Position(it[InstanceTable.x], it[InstanceTable.x]),
                    it[InstanceTable.visible].getEnum(),
                    Size.valueOf(it[InstanceTable.size])
                )
                Type.PNJ -> NonPlayableCharacter(
                    this.HP,
                    this.MP,
                    this.name,
                    ImageIcon(this.sprite),
                    Position(it[InstanceTable.x], it[InstanceTable.x]),
                    it[InstanceTable.visible].getEnum(),
                    Size.valueOf(it[InstanceTable.size]),
                    it[InstanceTable.currentHP],
                    it[InstanceTable.currentMP]
                )
                Type.PJ -> PlayableCharacter(
                    this.HP,
                    this.MP,
                    this.name,
                    ImageIcon(this.sprite),
                    Position(it[InstanceTable.x], it[InstanceTable.x]),
                    it[InstanceTable.visible].getEnum(),
                    Size.valueOf(it[InstanceTable.size]),
                    it[InstanceTable.currentHP],
                    it[InstanceTable.currentMP]
                )
            }
        }
    }

    return elements
}
