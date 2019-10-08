package jdr.exia.model.dao

import org.jetbrains.exposed.dao.IntIdTable

object ActTable : IntIdTable() {
    val name = varchar("name", 50)
    val idScene = integer("id_scene").references(SceneTable.id)
}

object SceneTable : IntIdTable() {
    val name = varchar("name", 50)
    val background = varchar("background", 200)
    val idAct = integer("id_act").references(ActTable.id)
}

object InstanceTable : IntIdTable() {
    val currentHP = integer("current_HP")
    val currentMP = integer("current_mana")
    val x = integer("x")
    val y = integer("y")
    val idAct = SceneTable.integer("id_act").references(ActTable.id)
}