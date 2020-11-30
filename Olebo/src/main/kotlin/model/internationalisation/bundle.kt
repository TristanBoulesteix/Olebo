@file:Suppress("unused", "ClassName")

package model.internationalisation

class StringsBundle_fr : Strings() {
    override val contents = mapOf(
            STR_VERSION to "version",
            STR_FILES to "fichiers",
            STR_TAKE_SCREENSHOT to "prendre une capture d'écran",
            STR_FILE_ALREADY_EXIST to "ce fichier existe déjà, voulez-vous le remplacer ?",
            STR_SAVE_AS to "enregistrer sous",
            STR_EXPORT_DATA to "exporter les données",
            STR_IMPORT_DATA to "importer les données",
            STR_AUTO_UPDATE to "mises à jour automatiques",
            STR_OLEBO_FILE to "fichier Olebo",
            ST_WARNING_CONFIG_RESET to "Attention ! Cette action va effacer toutes les données actuellement sauvegardées. Êtes-vous sûr de continuer ?",
            ST_CONFIGURATION_IMPORTED to "La configuration a bien été importée. Olebo va s'actualiser.",
    )
}

class StringsBundle : Strings() {
    override val contents = mapOf(
            STR_VERSION to "version",
            STR_FILES to "files",
            STR_TAKE_SCREENSHOT to "take a screenshot",
            STR_FILE_ALREADY_EXIST to "this file already exist. Do you want to replace it?",
            STR_SAVE_AS to "save as",
            STR_EXPORT_DATA to "export data",
            STR_IMPORT_DATA to "import data",
            STR_AUTO_UPDATE to "mises à jour automatiques",
            STR_OLEBO_FILE to "Olebo file",
            ST_WARNING_CONFIG_RESET to "Warning! This action will reset all data previously loaded. Do you want to continue?",
            ST_CONFIGURATION_IMPORTED to "The new configuaration was imported successfully. Olebo will reload.",
    )
}