package fr.olebo.persistence.tables

import fr.olebo.domain.models.Configurations

internal interface Initializable {
    fun initialize(configurations: Configurations)
}