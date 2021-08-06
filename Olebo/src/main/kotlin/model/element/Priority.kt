package jdr.exia.model.element

import jdr.exia.localization.STR_BACKGROUND
import jdr.exia.localization.STR_DEFAULT
import jdr.exia.localization.STR_FOREGROUND
import jdr.exia.localization.StringLocale
import jdr.exia.model.dao.PriorityTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

enum class Priority(val priority: PriorityElement, private val translationKey: String) {
    LOW(transaction { PriorityElement[1] }, STR_BACKGROUND),
    REGULAR(transaction { PriorityElement[2] }, STR_DEFAULT),
    HIGH(transaction { PriorityElement[3] }, STR_FOREGROUND);

    override fun toString() = StringLocale[translationKey]

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