package fr.olebo.domain.models

import org.kodein.di.*

/**
 * Type alias for a set of configuration items.
 *
 * This alias is used to define a collection of `ConfigurationItem` objects, representing various
 * configurations within the application. Provides a way to group multiple configurations and
 * allows for easy retrieval and manipulation.
 */
typealias Configurations = Set<ConfigurationItem>

/**
 * Marker interface for configuration items within the application's configuration system.
 *
 * This interface is used to group configuration items for dependency injection and retrieval.
 */
interface ConfigurationItem

/**
 * Retrieves an instance of the specified configuration item type from a list of configuration items.
 *
 * @return The first configuration item that matches the specified type [T], or throws an error if no matching configuration item is found.
 * @throws IllegalStateException if no matching configuration item is found.
 */
inline fun <reified T : ConfigurationItem> Configurations.get(): T =
    firstOrNull { it is T } as? T ?: error("No binding found for ${T::class.simpleName}")

/**
 * Adds a given configuration item to a set of `ConfigurationItem` bindings.
 *
 * This method allows you to dynamically append configuration items to the set of
 * configuration items managed by the dependency injection framework.
 *
 * @param provider The provider function that creates an instance of the configuration item.
 * The provider function has access to the `DirectDI` instance, which can be used to retrieve
 * other dependencies if needed.
 */
inline fun <reified T : ConfigurationItem> DI.Builder.appendConfiguration(crossinline provider: DirectDI.() -> T) =
    inBindSet<ConfigurationItem> {
        add {
            provider { provider(di.direct) }
        }
    }