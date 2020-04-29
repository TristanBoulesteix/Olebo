package jdr.exia.model.dao

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID

class Settings(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Settings>(SettingsTable)

    var value by SettingsTable.value
}