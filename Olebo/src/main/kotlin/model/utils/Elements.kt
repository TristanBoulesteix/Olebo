package model.utils

import model.element.Element

typealias Elements = MutableList<Element>

fun emptyElementsList(): Elements = mutableListOf()

fun Element.toElements(): Elements = mutableListOf(this)

fun <R> Elements.doIfContainsSingle(action: (Element) -> R) : R? {
    return if(size == 1) action(this[0]) else null
}