package fr.olebo.persistence.tables

import org.jetbrains.exposed.dao.id.IdTable

internal object TagTable: IdTable<String>() {
    override val id =  varchar("tagValue", 40).entityId()

    override val primaryKey = PrimaryKey(id)
}