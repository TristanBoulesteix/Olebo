package model.element

import model.dao.DAO
import model.dao.TypeTable
import model.internationalisation.*
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * The type of an element
 *
 * @param type A TypeElement object. It is the link between the enum and the database
 */
enum class Type(val type: TypeElement, private val typeNameKey: String) {
    OBJECT(transaction(DAO.database) { TypeElement[1] }, STR_OBJECT),
    PJ(transaction(DAO.database) { TypeElement[2] }, STR_PC),
    PNJ(transaction(DAO.database) { TypeElement[3] }, STR_NPC),
    BASIC(transaction(DAO.database) { TypeElement[4] }, STR_BASE_ELEMENTS);

    val typeName by StringDelegate(typeNameKey)

    /**
     * This class is the link between the enum and the database
     */
    class TypeElement(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, TypeElement>(TypeTable)

        private val actualName by TypeTable.name

        val name
            get() = typeElement.typeName

        /**
         * Get the enum which is linked to the databse
         *
         * @see model.element.Type
         */
        val typeElement
            get() = when (this.actualName) {
                "Basic" -> BASIC
                "PJ" -> PJ
                "PNJ" -> PNJ
                else -> OBJECT
            }
    }
}