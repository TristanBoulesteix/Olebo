package fr.olebo.persistence.tables

import fr.olebo.domain.models.scenario.Layer

internal object LayerTable : EnumInitializable<Layer>(enumValues()) {
    override val enumValue = enumeration<Layer>("layer")
}