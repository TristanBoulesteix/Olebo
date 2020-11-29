@file:Suppress("unused", "ClassName")

package model.internationalisation

class StringsBundle_fr : Strings() {
    override val contents = mapOf(
            STR_VERSION to "version",
            STR_FILES to "fichiers",
            STR_TAKE_SCREENSHOT to "prendre une capture d'écran"
    )
}

class StringsBundle : Strings() {
    override val contents = mapOf(
            STR_VERSION to "version",
            STR_FILES to "files",
            STR_TAKE_SCREENSHOT to "take a screenshot"
    )
}