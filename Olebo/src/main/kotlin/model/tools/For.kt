package jdr.exia.model.tools

inline fun <T> Collection<T>.forElse(block: (T) -> Unit) = if (isEmpty()) null else forEach(block)