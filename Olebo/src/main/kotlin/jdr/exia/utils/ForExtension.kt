package jdr.exia.utils

inline fun <T> Collection<T>.forElse(block: (T) -> Unit) = if (isEmpty()) null else forEach(block)