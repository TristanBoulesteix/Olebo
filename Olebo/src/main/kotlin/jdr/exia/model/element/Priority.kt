package jdr.exia.model.element

import jdr.exia.model.dao.PriorityTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

enum class Priority(val priority: PriorityElement) {
    LOW(transaction { PriorityElement[1] }),
    REGULAR(transaction { PriorityElement[2] }),
    HIGH(transaction { PriorityElement[3] });

    class PriorityElement(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, PriorityElement>(PriorityTable)

        private val actualName by PriorityTable.priority

        val priorityElement
            get() = when (this.actualName) {
                "LOW" -> LOW
                "HIGH" -> HIGH
                else -> REGULAR
            }
    }
}