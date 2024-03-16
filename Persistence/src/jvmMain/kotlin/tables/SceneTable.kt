package fr.olebo.persistence.tables

import org.jetbrains.exposed.dao.id.IntIdTable

internal object SceneTable : IntIdTable() {
    val name = varchar("name", 50)
    val background = varchar("background", 200)
    val idAct = integer("id_act").references(ActTable.id)
}