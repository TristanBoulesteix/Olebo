package jdr.exia.model.tools

import jdr.exia.model.element.Element

inline fun <R> List<Element>.doIfContainsSingle(defaultValue: R, action: (Element) -> R) =
    if (size == 1) action(this.first()) else defaultValue