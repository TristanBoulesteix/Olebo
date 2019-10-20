package jdr.exia.model.element

import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.SizeTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

enum class Size(val size: SizeElement) {
    XS(transaction(DAO.database) { SizeElement[1] }), S(transaction(DAO.database) { SizeElement[2] }), M(transaction(DAO.database) { SizeElement[3] }),
    L(transaction(DAO.database) { SizeElement[4] }), XL(transaction(DAO.database) { SizeElement[5] }),
    XXL(transaction(DAO.database) { SizeElement[6] });

    class SizeElement(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, SizeElement>(SizeTable)

        private val sizeName by SizeTable.size
        val absoluteSizeValue by SizeTable.value
        val sizeElement
            get() = valueOf(sizeName)
    }
}