package jdr.exia.model.dao.tables

import org.jetbrains.exposed.dao.IntIdTable

object SceneTable : IntIdTable() {
    val name = varchar("name", 50)
    val background = varchar("background", 200)
    val idAct = integer("id_act").references(ActTable.id)
}