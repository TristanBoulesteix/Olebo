package jdr.exia.utils

fun <T> Collection<T>.forElse(block: (T) -> Unit) = if (isEmpty()) null else forEach(block)

fun String.encodeQuotes() = this.replace("\"", "\\\"")