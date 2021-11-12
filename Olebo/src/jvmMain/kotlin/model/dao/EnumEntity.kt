package jdr.exia.model.dao

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.IdTable

abstract class EnumEntity<out T : Entity<Int>, E : Enum<E>>(table: IdTable<Int>) : EntityClass<Int, T>(table) {
    operator fun get(enum: E) = get(enum.ordinal + 1)
}