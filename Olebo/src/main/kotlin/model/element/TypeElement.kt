package jdr.exia.model.element

import jdr.exia.localization.*
import jdr.exia.model.dao.EnumEntity
import jdr.exia.model.dao.TypeTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.id.EntityID

/**
 * The type of element
 *
 * @param typeNameKey This is used to translate the name of the [TypeElement]
 */
enum class TypeElement(typeNameKey: String) {
    Object(STR_OBJECT),
    PJ(STR_PC),
    PNJ(STR_NPC),
    Basic(STR_BASE_ELEMENT);

    val localizedName = StringLocale[typeNameKey]

    /**
     * This class is the link between the enum and the database
     */
    class TypeEntity(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EnumEntity<TypeEntity, TypeElement>(TypeTable)

        val type by TypeTable.enumValue
    }
}