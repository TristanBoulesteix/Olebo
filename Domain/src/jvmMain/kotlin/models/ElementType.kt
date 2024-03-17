package fr.olebo.domain.models

/**
 * The type of element
 *
 */
enum class ElementType {
    Object, PJ, PNJ, Basic;

    val isCustom
        get() = this != Basic
}