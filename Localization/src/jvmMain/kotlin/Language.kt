package jdr.exia.localization

import java.util.*

@JvmInline
value class Language(val locale: Locale) {
    constructor(locale: String) : this(Locale(locale))

    override fun toString() = locale.getDisplayLanguage(locale)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

    companion object {
        fun getDefault() = Language(Locale.getDefault())
    }
}

val Language.languageCode: String
    get() = locale.language