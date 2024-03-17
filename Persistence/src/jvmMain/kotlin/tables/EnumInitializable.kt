package fr.olebo.persistence.tables

import fr.olebo.domain.models.Configurations
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insertIgnore

internal sealed class EnumInitializable<E : Enum<E>>(internal val values: Array<E>) : IntIdTable(), Initializable {
    abstract val enumValue: Column<E>

    final override fun initialize(configurations: Configurations) {
        values.forEachIndexed { index, enum ->
            val idEnum = index + 1

            insertIgnore {
                it[id] = EntityID(idEnum, this@EnumInitializable)
                it[enumValue] = enum
            }
        }
    }
}