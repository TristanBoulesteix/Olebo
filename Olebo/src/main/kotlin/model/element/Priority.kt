package model.element

import model.dao.DAO
import model.dao.PriorityTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

enum class Priority(val priority: PriorityElement, val priorityName: String) {
    LOW(transaction(DAO.database) { PriorityElement[1] }, "Arrière plan"),
    REGULAR(transaction(DAO.database) { PriorityElement[2] }, "Défaut"),
    HIGH(transaction(DAO.database) { PriorityElement[3] }, "Premier plan");

    class PriorityElement(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, PriorityElement>(PriorityTable)

        private val actualName by PriorityTable.priority

        val name
            get() = priorityElement.priorityName

        val priorityElement
            get() = when (this.actualName) {
                "LOW" -> LOW
                "HIGH" -> HIGH
                else -> REGULAR
            }
    }
}