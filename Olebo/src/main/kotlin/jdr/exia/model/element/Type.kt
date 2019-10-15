package jdr.exia.model.element

import jdr.exia.model.dao.TypeTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID

enum class Type(val type: TypeElement) {
    OBJECT(TypeElement[1]), PJ(TypeElement[2]), PNJ(TypeElement[3]);

    class TypeElement(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, TypeElement>(TypeTable)

        val name by TypeTable.name

        fun getTypeWithTypeElement(): Type {
            return if (this.name == "Object") OBJECT else if (this.name == "PJ") PJ else PNJ
        }
    }
}