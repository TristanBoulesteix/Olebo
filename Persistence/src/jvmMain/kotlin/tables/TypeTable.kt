package fr.olebo.persistence.tables

import fr.olebo.domain.models.ElementType

internal object TypeTable : EnumInitializable<ElementType>(enumValues()) {
    override val enumValue = enumerationByName<ElementType>("type", 50)
}