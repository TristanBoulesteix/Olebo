package jdr.exia.model.tools

import jdr.exia.model.element.Element
import org.jetbrains.exposed.sql.SizedCollection

inline fun <R> List<Element>.doIfContainsSingle(defaultValue: R, action: (Element) -> R) =
    if (size == 1) action(this.first()) else defaultValue

fun <T> T.toSingletonList(): List<T & Any> {
    require(this !is Collection<*>) { "L'argument est déjà une collection !" }

    return if (this != null) listOf(this) else emptyList()
}

fun <T> Collection<T>.toSizedCollection() = SizedCollection(this)