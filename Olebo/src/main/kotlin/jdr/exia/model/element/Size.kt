package jdr.exia.model.element

import jdr.exia.model.dao.SizeTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * The size of an element
 *
 * @param size A SizeElement object. It is the link between the enum and the database
 */
enum class Size(val size: SizeElement) {
    XS(transaction { SizeElement[1] }), S(transaction { SizeElement[2] }), M(transaction { SizeElement[3] }),
    L(transaction { SizeElement[4] }), XL(transaction { SizeElement[5] }),
    XXL(transaction { SizeElement[6] });

    companion object {
        val DEFAULT = S
    }

    /**
     * This class is the link between the enum and the databse
     */
    class SizeElement(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, SizeElement>(SizeTable)

        private val sizeName by SizeTable.size
        val absoluteSizeValue by SizeTable.value

        /**
         * Get the enum which is linked to the databse
         *
         * @see [Size]
         */
        val sizeElement
            get() = valueOf(sizeName)
    }
}