package jdr.exia.model.utils

import kotlin.reflect.KMutableProperty0

/**
 * Assign a value to a mutable property if the value is different from the previous one
 */
fun <T> KMutableProperty0<T>.assignIfDifferent(value: T) {
    if (get() != value)
        set(value)
}