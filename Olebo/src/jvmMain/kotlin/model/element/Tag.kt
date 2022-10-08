package jdr.exia.model.element

import jdr.exia.model.dao.TagTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Tag(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Tag>(TagTable) {
        fun newFrom(value: String) = new(value) { }
    }

    val value
        get() = id.value
}