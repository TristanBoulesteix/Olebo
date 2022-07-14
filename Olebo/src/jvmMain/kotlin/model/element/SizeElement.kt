package jdr.exia.model.element

import jdr.exia.model.dao.EnumEntity
import jdr.exia.model.dao.SizeTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.id.EntityID

/**
 * The size of an element
 *
 * @param value An int to represent the size.
 */
enum class SizeElement(val value: Int) {
    XS(30), S(60), M(120), L(200), XL(300), XXL(400);

    companion object {
        inline val DEFAULT
            get() = S
    }

    /**
     * This class is the link between the enum and the database
     */
    class SizeEntity(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EnumEntity<SizeEntity, SizeElement>(SizeTable)

        val size by SizeTable.enumValue
    }
}