package jdr.exia.model.utils

import jdr.exia.model.element.Element

class Elements(elements: List<Element>) : MutableList<Element> by elements.toMutableList() {
    constructor() : this(mutableListOf())

    constructor(element: Element) : this(listOf(element))

    constructor(vararg elements: Element) : this(listOf(*elements))

    inline fun <R> doIfContainsSingle(action: (Element) -> R) = if (size == 1) action(this[0]) else null
}

fun emptyElements(): Elements = Elements()

fun Element.toElements() = Elements(this)

