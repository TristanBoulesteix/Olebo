@file:Suppress("unused", "ClassName")

package model.internationalisation

class StringsBundle_fr : Strings() {
    override val contents = mapOf(
            STR_VERSION to "version",
            STR_FILES to "fichiers",
            STR_TAKE_SCREENSHOT to "prendre une capture d'écran",
            STR_FILE_ALREADY_EXIST to "ce fichier existe déjà, voulez-vous le remplacer ?",
            STR_SAVE_AS to "enregistrer sous"
    )
}

class StringsBundle : Strings() {
    override val contents = mapOf(
            STR_VERSION to "version",
            STR_FILES to "files",
            STR_TAKE_SCREENSHOT to "take a screenshot",
            STR_FILE_ALREADY_EXIST to "this file already exist. Do you want to replace it?",
            STR_SAVE_AS to "save as"
    )
}