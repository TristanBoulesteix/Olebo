package fr.olebo.persistence.tables

import fr.olebo.domain.models.ElementSize

internal object SizeTable : EnumInitializable<ElementSize>(enumValues()) {
    override val enumValue = enumerationByName<ElementSize>("size", 50)
}