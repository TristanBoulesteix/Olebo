@file:Suppress("unused", "ClassName")

package model.internationalisation

/**
 * French strings
 */
class StringsBundle_fr : Strings() {
    override val contents = mapOf(
            STR_VERSION to "version",
            STR_FILES to "fichiers",
            STR_TAKE_SCREENSHOT to "prendre une capture d'écran",
            ST_FILE_ALREADY_EXISTS to "Ce fichier existe déjà, voulez-vous le remplacer ?",
            STR_SAVE_AS to "enregistrer sous",
            STR_EXPORT_DATA to "exporter les données",
            STR_IMPORT_DATA to "importer les données",
            STR_AUTO_UPDATE to "mises à jour automatiques",
            STR_OLEBO_FILE to "fichier Olebo",
            ST_WARNING_CONFIG_RESET to "Attention ! Cette action va effacer toutes les données actuellement sauvegardées. Êtes-vous sûr de continuer ?",
            ST_CONFIGURATION_IMPORTED to "La configuration a bien été importée. Olebo va s'actualiser.",
            ST_WARNING_PREVIOUS_VERSION_FILE to "Ce fichier semble provenir d'une version ultérieur d'Olebo. Veuillez mettre à jour le logiciel pour importer le fichier.",
            ST_WARNING_MISSING_CONF_FILES to "Des fichiers de configuration sont manquants. Impossible d'importer les données.",
            ST_UNKNOWN_ERROR to "Une erreur inconnue s'est produite.",
            ST_FILE_MAY_BE_CORRUPTED to "Le fichier peut être corrompu.",
            STR_ABOUT to "à propos",
            STR_APP_VERSION to "version de l'application :",
            STR_DATABASE_VERSION to "version de la base de données :",
            STR_ELEMENTS to "éléments",
            STR_ADD_ACT to "ajouter un scénario",
            STR_OBJECT_LIST to "liste des objets",
            STR_OBJECTS to "Objets",
            STR_PC to "PJ",
            STR_NPC to "PNJ",
            STR_NEW_ACT to "nouveau scénario",
            STR_NAME to "nom",
            STR_CONFIRM to "valider",
            ST_ACT_ALREADY_EXISTS to "Désolé, un scénario avec le même nom existe déjà ou les données du scénario sont invalides !",
            STR_NEW_SCENE to "nouvelle scène",
            STR_CHANGE_SCENE to "modification de la scène",
            STR_NAME_OF_SCENE to "nom de la scène :",
            STR_IMPORT_IMG to "importer une image",
            STR_CANCEL to "annuler",
            ST_ACT_ALREADY_EXISTS to "Une scène avec ce nom existe déjà ou le fichier sélectionné est invalide !",
    )
}

/**
 * Default strings (English strings)
 */
class StringsBundle : Strings() {
    override val contents = mapOf(
            STR_VERSION to "version",
            STR_FILES to "files",
            STR_TAKE_SCREENSHOT to "take a screenshot",
            ST_FILE_ALREADY_EXISTS to "This file already exists. Do you want to replace it?",
            STR_SAVE_AS to "save as",
            STR_EXPORT_DATA to "export data",
            STR_IMPORT_DATA to "import data",
            STR_AUTO_UPDATE to "mises à jour automatiques",
            STR_OLEBO_FILE to "Olebo file",
            ST_WARNING_CONFIG_RESET to "Warning! This action will reset all data previously loaded. Do you want to continue?",
            ST_CONFIGURATION_IMPORTED to "The new configuaration was imported successfully. Olebo will reload.",
            ST_WARNING_PREVIOUS_VERSION_FILE to "This file seems to come from a previous version of Olebo. Please, update the software to import this file.",
            ST_WARNING_MISSING_CONF_FILES to "Some configuration files are missing. Unable to import data.",
            ST_UNKNOWN_ERROR to "An unknown error has occured.",
            ST_FILE_MAY_BE_CORRUPTED to "This file may be corrupted.",
            STR_ABOUT to "about",
            STR_APP_VERSION to "app version:",
            STR_DATABASE_VERSION to "database version:",
            STR_ELEMENTS to "elements",
            STR_ADD_ACT to "new scenario",
            STR_OBJECT_LIST to "Objects list",
            STR_OBJECTS to "Objects",
            STR_PC to "PC",
            STR_NPC to "NPC",
            STR_NEW_ACT to "new scenario",
            STR_NAME to "nom",
            STR_CONFIRM to "confirm",
            ST_ACT_ALREADY_EXISTS to "Sorry, a scenario with this name already exists or the data entered are invalid.",
            STR_NEW_SCENE to "new scene",
            STR_CHANGE_SCENE to "modify scene",
            STR_NAME_OF_SCENE to "name of the scene:",
            STR_IMPORT_IMG to "import an image",
            STR_CANCEL to "annuler",
            ST_ACT_ALREADY_EXISTS to "A scene with this scene already exists or the selected file is invalid.",
    )
}