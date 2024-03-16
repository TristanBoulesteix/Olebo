package fr.olebo.persistence.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

internal object ActTable : IntIdTable() {
    val name = varchar("name", 50)
    val scene = reference("id_scene", SceneTable).default(EntityID(0, SceneTable))
}