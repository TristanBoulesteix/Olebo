package model.utils

import model.element.Blueprint
import model.element.Element
import model.element.Type

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
fun Boolean.toInt(): Int = if (this) 1 else 0

/**
 * Convert a String to the corresponding boolean
 *
 * @return true if the value is "true"
 */
fun String?.toBoolean(): Boolean = this?.toLowerCase() == "true"

/**
 * Check if element is a PNJ or a PJ
 *
 * @return true if it's a character
 */
fun Element?.isCharacter(): Boolean {
    return this != null && (this.type.typeElement == Type.PNJ || this.type.typeElement == Type.PJ)
}

/**
 * Check if blueprint is a PNJ or a PJ
 *
 * @return true if it's a character
 */
fun Blueprint?.isCharacter(): Boolean {
    return this != null && (this.type.typeElement == Type.PNJ || this.type.typeElement == Type.PJ)
}