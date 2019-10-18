package jdr.exia.model.element

import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.TypeTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

enum class Type(val type: TypeElement) {
    OBJECT(transaction(DAO.database) { TypeElement[1] }),
    PJ(transaction(DAO.database) { TypeElement[2] }),
    PNJ(transaction(DAO.database) { TypeElement[3] });

    class TypeElement(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, TypeElement>(TypeTable)

        val name by TypeTable.name

        val typeElement
            get() = if (this.name == "Object") OBJECT else if (this.name == "PJ") PJ else PNJ
    }
}