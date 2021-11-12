package jdr.exia.model.element

import jdr.exia.localization.STR_BACKGROUND
import jdr.exia.localization.STR_DEFAULT
import jdr.exia.localization.STR_FOREGROUND
import jdr.exia.localization.StringLocale
import jdr.exia.model.dao.EnumEntity
import jdr.exia.model.dao.LayerTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.id.EntityID

enum class Layer(private val translationKey: String) {
    LOW(STR_BACKGROUND),
    REGULAR(STR_DEFAULT),
    HIGH(STR_FOREGROUND);

    override fun toString() = StringLocale[translationKey]

    class LayerEntity(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EnumEntity<LayerEntity, Layer>(LayerTable)

        val layer by LayerTable.enumValue
    }
}