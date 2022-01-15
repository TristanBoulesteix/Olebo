package jdr.exia.localization

actual sealed class StringLocale {
    /**
     * The [invoke] method of the companion object need to be called in order to initialize the right locale to use
     */
    actual companion object {
        @PublishedApi
        internal actual var activeLanguage: Language
            get() = TODO("Not yet implemented")
            set(value) {}

        internal actual val langBundle: ResourceBundle
            get() = TODO("Not yet implemented")

        actual operator fun get(
            key: String,
            state: StringStates,
            vararg args: Any?
        ): String {
            TODO("Not yet implemented")
        }
    }

    internal actual abstract val contents: Map<String, String>
}