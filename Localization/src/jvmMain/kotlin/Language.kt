@file:JvmName("Language-impl")

package jdr.exia.localization

import java.util.Locale as LocaleJVM

actual typealias Locale = LocaleJVM

@JvmInline
actual value class Language(val locale: Locale) {
    override fun toString() = locale.getDisplayLanguage(locale)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

    actual companion object {
        fun getDefault() = Language(Locale.getDefault())

        internal actual inline val english
            get() = Language(Locale.ENGLISH)

        internal actual inline val french
            get() = Language(Locale.FRENCH)
    }
}

fun Language(locale: String) = Language(Locale(locale))

actual val Language.languageCode: String
    get() = locale.language