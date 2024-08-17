package fr.olebo.domain.models.scenario

/**
 * The type of element
 *
 */
enum class ElementType {
    Object, PJ, PNJ, Basic;

    val isCustom
        get() = this != Basic
}