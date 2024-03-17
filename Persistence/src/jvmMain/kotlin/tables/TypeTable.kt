package fr.olebo.persistence.tables

import fr.olebo.domain.models.TypeElement

internal object TypeTable : EnumInitializable<TypeElement>(enumValues()) {
    override val enumValue = enumerationByName<TypeElement>("type", 50)
}