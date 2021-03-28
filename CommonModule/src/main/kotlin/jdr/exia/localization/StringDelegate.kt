package jdr.exia.localization

import kotlin.reflect.KProperty

class StringDelegate<in Owner>(private val key: String) {
    operator fun getValue(thisRef: Owner, property: KProperty<*>) = StringLocale[key]
}