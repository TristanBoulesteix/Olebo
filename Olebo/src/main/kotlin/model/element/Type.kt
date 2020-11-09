package model.element

import model.dao.DAO
import model.dao.TypeTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * The type of an element
 *
 * @param type A TypeElement object. It is the link between the enum and the database
 */
enum class Type(val type: TypeElement) {
    OBJECT(transaction(DAO.database) { TypeElement[1] }),
    PJ(transaction(DAO.database) { TypeElement[2] }),
    PNJ(transaction(DAO.database) { TypeElement[3] });

    /**
     * This class is the link between the enum and the databse
     */
    class TypeElement(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, TypeElement>(TypeTable)

        val name by TypeTable.name

        /**
         * Get the enum which is linked to the databse
         *
         * @see model.element.Type
         */
        val typeElement
            get() = if (this.name == "Object") OBJECT else if (this.name == "PJ") PJ else PNJ
    }
}