package fr.olebo.persistence.tables

import fr.olebo.domain.models.scenario.ElementSize

internal object SizeTable : EnumInitializable<ElementSize>(enumValues()) {
    override val enumValue = enumerationByName<ElementSize>("size", 50)
}