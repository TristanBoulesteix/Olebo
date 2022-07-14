@file:Suppress("ClassName")

package jdr.exia.localization

/**
 * Default strings (English strings)
 */
internal expect class StringLocaleBundle {
    internal val contents: Map<String, String>
}

/**
 * French strings
 */
@Suppress("unused")
internal expect class StringLocaleBundle_fr {
    internal val contents: Map<String, String>
}