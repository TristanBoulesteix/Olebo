package jdr.exia.model.utils

/**
 * Convert an Int to the corresponding boolean
 *
 * @return true if the value of the integer is not 0
 */
fun Int.toBoolean() = this != 0

/**
 * Convert a Boolean to the corresponding integer
 *
 * @return 1 if true
 */
fun Boolean.toInt(): Int = if(this) 1 else 0