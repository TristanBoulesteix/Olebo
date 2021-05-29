package jdr.exia.model.element

import jdr.exia.localization.*
import jdr.exia.model.dao.TypeTable
import jdr.exia.model.element.Type.TypeElement
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * The type of an element
 *
 * @param type A [TypeElement] object. It is the link between the enum and the database
 */
enum class Type(val type: TypeElement, typeNameKey: String) {
    OBJECT(transaction { TypeElement[1] }, STR_OBJECT),
    PJ(transaction { TypeElement[2] }, STR_PC),
    PNJ(transaction { TypeElement[3] }, STR_NPC),
    BASIC(transaction { TypeElement[4] }, STR_BASE_ELEMENT);

    val localizedName by StringDelegate(typeNameKey)

    /**
     * This class is the link between the enum and the database
     */
    class TypeElement(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, TypeElement>(TypeTable)

        private val actualName by TypeTable.name

        val name
            get() = typeElement.localizedName

        /**
         * Get the enum which is linked to the databse
         *
         * @see [Type]
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