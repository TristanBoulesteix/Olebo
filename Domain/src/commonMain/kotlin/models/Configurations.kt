package fr.olebo.domain.models

import org.kodein.di.*

typealias Configurations = Set<ConfigurationItem>

interface ConfigurationItem

inline fun <reified T : ConfigurationItem> Configurations.get(): T =
    firstOrNull { it is T } as? T ?: error("No binding found for ${T::class.simpleName}")

inline fun <reified T : ConfigurationItem> DI.Builder.appendConfiguration(crossinline provider: DirectDI.() -> T) =
    inBindSet<ConfigurationItem> {
        add {
            provider { provider(di.direct) }
        }
    }