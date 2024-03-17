package fr.olebo.domain.models

/**
 * The type of element
 *
 */
enum class TypeElement {
    Object, PJ, PNJ, Basic;

    val isCustom
        get() = this != Basic
}