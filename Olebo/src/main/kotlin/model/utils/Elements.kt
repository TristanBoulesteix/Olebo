package model.utils

import model.element.Element

typealias Elements = List<Element>

typealias MutableElements = MutableList<Element>

fun mutableEmptyElements() : MutableElements = mutableListOf()

fun emptyElements(): Elements = mutableEmptyElements().toList()

fun Element.toElements(): MutableElements = mutableListOf(this)

fun <R> Elements.doIfContainsSingle(action: (Element) -> R) : R? {
    return if(size == 1) action(this[0]) else null
}