package jdr.exia.localization

actual abstract class ResourceBundle {
    abstract val bundle: StringLocale

    actual fun getString(key: String) = bundle.contentBuilder.getOrElse(key) { key }
}

fun ResourceBundle(language: Language) = object : ResourceBundle() {
    override val bundle = if (language == Language.french) StringLocaleBundle_fr() else StringLocaleBundle()
}