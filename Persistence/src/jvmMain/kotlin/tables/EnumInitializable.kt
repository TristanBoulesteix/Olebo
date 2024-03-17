package fr.olebo.persistence.tables

import fr.olebo.domain.models.Configurations
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

internal sealed class EnumInitializable<E : Enum<E>>(private val values: Array<E>) : IntIdTable(), Initializable {
    abstract val enumValue: Column<E>

    final override fun initialize(configurations: Configurations) {
        values.forEachIndexed { index, enum ->
            val idEnum = index + 1

            if (selectAll().where { (id eq idEnum) and (enumValue eq enum) }.count() <= 0) {
                insert {
                    it[id] = EntityID(idEnum, this@EnumInitializable)
                    it[enumValue] = enum
                }
            }
        }
    }
}