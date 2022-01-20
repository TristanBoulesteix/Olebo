@file:Suppress("ClassName")

package jdr.exia.localization

/**
 * Default strings (English strings)
 */
internal actual class StringLocaleBundle : StringLocale() {
    actual override val contents = mapOf(
        STR_VERSION to "version",
        STR_FILES to "files",
        STR_TAKE_SCREENSHOT to "take a screenshot",
        ST_FILE_ALREADY_EXISTS to "This file already exists. Do you want to replace it?",
        STR_SAVE_AS to "save as",
        STR_EXPORT_DATA to "export data",
        STR_IMPORT_DATA to "import data",
        STR_AUTO_UPDATE to "automatic updates",
        STR_OLEBO_FILE to "Olebo file",
        ST_WARNING_CONFIG_RESET to "Warning! This action will reset all data previously loaded. Do you want to continue?",
        ST_CONFIGURATION_IMPORTED to "The new configuration was imported successfully. Olebo will reload.",
        ST_WARNING_PREVIOUS_VERSION_FILE to "This file seems to come from a previous version of Olebo. Please, update the software to import this file.",
        ST_WARNING_MISSING_CONF_FILES to "Some configuration files are missing. Unable to import data.",
        ST_UNKNOWN_ERROR to "An unknown error has occurred.",
        ST_FILE_MAY_BE_CORRUPTED to "This file may be corrupted.",
        STR_ABOUT to "about",
        STR_APP_VERSION to "app version:",
        STR_ELEMENTS to "elements",
        STR_ADD_ACT to "create a new scenario",
        STR_OBJECT_LIST to "Objects list",
        STR_OBJECTS to "Objects",
        STR_PC to "PC",
        STR_NPC to "NPC",
        STR_NEW_ACT to "new scenario",
        STR_NAME to "nom",
        STR_CONFIRM to "confirm",
        ST_ACT_ALREADY_EXISTS to "Sorry, a scenario with this name already exists or the data entered are invalid.",
        ST_ACT_WITHOUT_SCENE to "A scenario need to contains at least one scene.",
        STR_NEW_SCENE to "new scene",
        STR_CHANGE_SCENE to "modify scene",
        STR_NAME_OF_SCENE to "name of the scene:",
        STR_IMPORT_IMG to "import an image",
        STR_IMPORT_NEW_IMG to "import a new image",
        STR_CANCEL to "cancel",
        ST_ACT_ALREADY_EXISTS to "A scene with this scene already exists or the selected file is invalid.",
        STR_SCENES to "scenes",
        STR_NEW_ELEMENT to "new element",
        STR_CHANGE_ELEMENT to "update element",
        STR_NAME_OF_ELEMENT to "name of the element:",
        STR_MAX_HEALTH to "HP max:",
        STR_MAX_MANA to "MP max:",
        ST_ELEMENT_ALREADY_EXISTS_OR_INVALID to "An element with this name already exists or the selected file is invalid.",
        STR_SEARCH to "search",
        STR_BASE_ELEMENT_PLR to "basic elements",
        STR_BASE_ELEMENT to "basic element",
        STR_NO_ELEMENT to "no element",
        STR_DM_TITLE_FRAME to "DM window",
        STR_PLAYER_TITLE_FRAME to "players window",
        STR_TOOLS to "tools",
        STR_ENABLE_CURSOR to "enable cursor on Players window",
        STR_RESTORE to "restore",
        STR_WINDOW to "window",
        STR_CLOSE_ACT to "close this act",
        STR_TOGGLE_PLAYER_FRAME to "toggle Players window",
        STR_CHOOSE_SCENE to "choose a scene",
        STR_IS_CURRENT_SCENE to "current",
        STR_TOKENS to "tokens",
        STR_MANAGE_BLUEPRINTS to "manage blueprints",
        STR_IMPORT_FROM_SCENE to "import from an other scene",
        STR_IMPORT_ALL_ELEMENTS to "import all elements",
        STR_DELETE_SELECTED_TOKENS to "delete selected token(s)",
        STR_CLEAR_BOARD to "clear board",
        STR_DELETION to "delete all elements",
        ST_CONFIRM_CLEAR_BOARD to "Do you really want to remove all elements from the scene? This action is not cancelable.",
        STR_ROTATE_TO_RIGHT to "rotate to right",
        STR_ROTATE_TO_LEFT to "rotate to left",
        STR_VISIBILITY to "visibility",
        STR_SHOW to "hide",
        STR_HIDE to "show",
        STR_DELETE to "delete",
        STR_DEFAULT to "default",
        STR_FOREGROUND to "foreground",
        STR_BACKGROUND to "background",
        STR_SELECTED_ELEMENTS to "selected elements",
        STR_NO_SELECTED_ELEMENT to "no element selected",
        STR_HP to "HP",
        STR_MP to "MP",
        STR_IMG to "icon",
        STR_SMALL_IMG to "img",
        STR_WARNING to "warning!",
        ST_SCENE_ALREADY_EXISTS to "A scene with the same name already exists.",
        ST_ELEMENT_ALREADY_EXISTS to "An element with this name already exists.",
        STR_CRITICAL_ERROR to "critical error",
        ST_ERROR_LOADING_DATABASE to """
            A critical error has occurred. Unable to launch Olebo.
            If the error persists, you can try resetting the database.
            Warning : This action will erase permanently all your data.
        """.trimIndent(),
        STR_ERROR to "error",
        ST_NEW_VERSION_AVAILABLE to "A new version of the Olebo software is available. Do you want to install it?",
        STR_UPDATE_AVAILABLE to "update available",
        STR_YES to "yes",
        STR_NO to "no",
        STR_PREPARE_UPDATE to "preparation of the update",
        ST_UPDATE_OLEBO_RESTART to "Olebo will restart once the update is completed.",
        ST_NEVER_ASK_UPDATE to "No, never ask again for this version",
        ST_ERROR_ACT_NOT_EXISTS to "Error. This scenario doesn't seem to exist.",
        ST_UNKNOWN_DATABASE_VERSION to "Database error ! Unknown database version.",
        STR_TRANSPARENT_POINTER to "transparent pointer",
        STR_WHITE_POINTER to "white pointer",
        STR_BLUE_POINTER to "blue pointer",
        STR_GREEN_POINTER to "green pointer",
        ST_CHANGE_VISIBILITY to "Change visibility of an element",
        ST_CHANGE_VISIBILITY_PLR to "Change visibility of elements",
        STR_RESIZE_ELEMENT to "resize an element",
        STR_RESIZE_ELEMENT_PLR to "resize elements",
        STR_MOVE_ELEMENT to "move an element",
        STR_MOVE_ELEMENTS to "move elements",
        STR_OBJECT to "object",
        STR_OPTIONS to "options",
        STR_GENERAL to "general",
        STR_SOFTWARE_LANGUAGE_LABEL to "language:",
        STR_LOOK_AND_FEEL to "look and feel",
        STR_CURSOR_COLOR_LABEL to "cursor color:",
        ST_LANGUAGE_CHANGE_ON_RESTART to "Language changes will apply at the next restart.",
        STR_SAVE to "save",
        STR_RESTORE_DEFAULTS_OPTIONS to "restore default settings",
        STR_CUSTOM_COLOR to "custom color",
        STR_PURPLE to "purple",
        STR_WHITE_WITH_BLACK_BORDER to "white with black border",
        STR_BLACK_WITH_WHITE_BORDER to "black with white border",
        STR_SELECT_COLOR to "pick a color",
        STR_PLAYER_FRAME_OPENED to "automatically open the players window",
        STR_DOUBLE_CLICK_OPEN_ACT to "double click to open the scenario",
        STR_DEFAULT_ELEMENT_VISIBILITY to "tokens visible for players by default",
        STR_YELLOW to "yellow",
        STR_RED to "red",
        ST_OLEBO_IS_UPDATING to "Olebo is updating.",
        ST_NOT_TURN_OFF to "Don't turn off your computer.",
        ST_UPDATE_SUCCESS to "Olebo is successfully updated.",
        ST_UPDATE_FAILED to "The update of Olebo has failed.",
        ST_UPDATE_TRY_AGAIN to "Please, try again later.",
        STR_CANCEL_UPDATE to "cancel update",
        STR_LABEL_STATE to "label display:",
        STR_LABEL to "label",
        STR_SIZE_COMBO_TOOLTIP to "set the size of the element",
        STR_PRIORITY_COMBO_TOOLTIP to "set the priority level of the element",
        STR_LABEL_TOOLTIP to "add a custom label to the element",
        STR_DB_VERSION_MISMATCH to "error during database initialization",
        ST_DB_VERSION_MISMATCH_MESSAGE to """
            The database on this computer come is from a later version of Olebo. To lose the data, please update the application or reset it.
            If you choose to continue without update the software, some data could be lost and some features may be missing.
        """.trimIndent(),
        STR_EXIT to "exit",
        STR_UPDATE to "update",
        STR_RESET to "reset",
        ST_CANCEL_WILL_ERASE_CHANGES to "All changes will be canceled. Do you wish to continue?",
        STR_BACK to "go back",
        STR_LOADING to "loading",
        ST_NAME_OF_BLUEPRINT_REQUIRED to "The name of the element is required.",
        ST_IMG_BLUEPRINT_REQUIRED to "An image is required for an element.",
        ST_IMG_BLUEPRINT_NOT_EXIST_OR_INVALID to "The selected image doesn't exist or is invalid.",
        ST_INT1_OCCURRENCE_BLUEPRINT_TO_DELETE to """
            This element is used in %s scene(s).
            Are you sure you want to delete it? This action cannot be cancelled.
        """.trimIndent(),
        ST_OCCURRENCE_BLUEPRINT_TO_DELETE to """
            This element is used in 1 scene.
            Are you sure you want to delete it? This action cannot be cancelled.
        """.trimIndent(),
        ST_DEFAULT_SETTINGS_RESTORED to "The default settings have been successfully restored.",
        STR_INFO to "information",
        STR_BLACK to "black",
        STR_LABEL_COLOR to "label color:",
        STR_LABEL_DISABLED to "label disabled",
        STR_LABEL_VISIBLE to "label visible for both windows",
        STR_LABEL_VISIBLE_FOR_MASTER to "label only visible for the DM",
        STR_LABEL_HIDDEN to "label hidden",
        ST_ENTER_SCENE_NAME to "Enter a scene name",
        ST_CONFIRM_DELETE_ACT to "Are you sure to delete this scenario? This action cannot be canceled.",
        STR_DELETE_ACT to "delete scenario",
        STR_INSERT_ACT_NAME to "insert a scenario name",
        ST_ACT_WITHOUT_NAME to "A scenario must have a name.",
        STR_ADD_ELEMENT to "add an element",
        ST_STR1_DM_WINDOW_NAME to "Olebo - DM window - %s",
        STR_SIZE to "size:",
        STR_PRIORITY to "layer:",
        STR_CLOSE to "close",
        STR_DOWNLOAD_UPDATE to "download of the update",
        STR_DOWNLOAD to "download:",
        ST_OLEBO_SEARCH_FOR_UPDATE to "Olebo is checking for update",
        STR_SELECT_DOWN to "select previous token",
        STR_SELECT_UP to "select next token",
        STR_SELECT_ALL to "select all tokens",
        STR_CONFIRM_CREATE_SCENE to "save scene",
        STR_CONFIRM_EDIT_SCENE to "save scene",
        STR_CONFIRM_CREATE_ACT to "save scenario",
        STR_CONFIRM_EDIT_ACT to "save scenario",
        STR_OPEN_ACT_TOOLTIP to "open the scenario",
        STR_EDIT_ACT_TOOLTIP to "edit scenario",
        STR_EDIT_SCENE_TOOLTIP to "edit scene",
        STR_DELETE_SCENE_TOOLTIP to "delete scene",
        STR_CONTINUE to "continue",
        STR_VERSION_CODE to "version code:",
        STR_NO_ACT to "no scenario",
        STR_NO_SCENE to "no scene",
        ST_DEVELOPED_BY to "Developed by BOULESTEIX Tristan and ONFRAY Vincent.",
        ST_SHOULD_OPEN_PLAYER_FRAME_FULL_SCREEN to """
            Open the player window if there is a second screen
            (This feature may be disable for screens with different ratios / resolution)
        """.trimIndent(),
        ST_OLEBO_WEB_EXPLANATION to """
            ShareScene allows you to share the player view on the internet in real time.
            Only a web browser and an internet connection is required for the players.
            Please note that the token images will be compressed and may lose some quality.
            
            To start, press the button on the right and then share tot he players the URL provided.
        """.trimIndent(),
        STR_START_OLEBO_WEB to "start and connect",
        STR_LOGIN_OLEBO_WEB to "login...",
        STR_ERROR_LOGIN_TO_OLEBO_WEB to "connection failed - please check your internet connection",
        STR_EDIT_TAB to "Edition",
        STR_OLEBO_WEB_TAB to "ShareScene (Olebo Web)",
    )
}

/**
 * French strings
 */
@Suppress("unused")
internal actual class StringLocaleBundle_fr : StringLocale() {
    actual override val contents = mapOf(
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
        ST_ACT_WITHOUT_SCENE to "Un scénario doit contenir au moins une scène !",
        STR_NEW_SCENE to "nouvelle scène",
        STR_CHANGE_SCENE to "modification de la scène",
        STR_NAME_OF_SCENE to "nom de la scène :",
        STR_IMPORT_IMG to "importer une image",
        STR_IMPORT_NEW_IMG to "importer une nouvelle image",
        STR_CANCEL to "annuler",
        ST_SCENE_ALREADY_EXISTS_OR_INVALID to "Une scène avec ce nom existe déjà ou le fichier sélectionné est invalide !",
        STR_SCENES to "scènes",
        STR_NEW_ELEMENT to "nouvel élément",
        STR_CHANGE_ELEMENT to "modifier élément",
        STR_NAME_OF_ELEMENT to "nom de l'élément :",
        STR_MAX_HEALTH to "PV max :",
        STR_MAX_MANA to "PM max :",
        ST_ELEMENT_ALREADY_EXISTS_OR_INVALID to "Un élément avec ce nom existe déjà ou le fichier sélectionné est invalide !",
        STR_SEARCH to "rechercher",
        STR_BASE_ELEMENT_PLR to "éléments de base",
        STR_BASE_ELEMENT to "élément de base",
        STR_NO_ELEMENT to "aucun élément",
        STR_DM_TITLE_FRAME to "fenêtre MJ",
        STR_PLAYER_TITLE_FRAME to "fenêtre joueurs",
        STR_TOOLS to "outils",
        STR_ENABLE_CURSOR to "activer le curseur joueurs",
        STR_RESTORE to "restaurer",
        STR_WINDOW to "fenêtre",
        STR_CLOSE_ACT to "fermer scenario",
        STR_TOGGLE_PLAYER_FRAME to "fenetre joueurs ON/OFF",
        STR_CHOOSE_SCENE to "choisir une scène",
        STR_IS_CURRENT_SCENE to "actuelle",
        STR_TOKENS to "pions",
        STR_MANAGE_BLUEPRINTS to "gèrer les modèles d'éléments",
        STR_IMPORT_FROM_SCENE to "importer depuis une autre scène",
        STR_IMPORT_ALL_ELEMENTS to "tout importer",
        STR_DELETE_SELECTED_TOKENS to "supprimer pion(s) selectionné(s)",
        STR_CLEAR_BOARD to "vider le plateau",
        STR_DELETION to "suppression",
        ST_CONFIRM_CLEAR_BOARD to "Voulez-vous vraiment supprimer tous les éléments du plateau ? Cette action est irréversible.",
        STR_ROTATE_TO_RIGHT to "pivoter vers la droite",
        STR_ROTATE_TO_LEFT to "pivoter vers la gauche",
        STR_VISIBILITY to "visibilité",
        STR_SHOW to "afficher",
        STR_HIDE to "masquer",
        STR_DELETE to "supprimer",
        STR_DEFAULT to "défaut",
        STR_FOREGROUND to "premier plan",
        STR_BACKGROUND to "arrière plan",
        STR_SELECTED_ELEMENTS to "éléments sélectionnés",
        STR_NO_SELECTED_ELEMENT to "aucun élément sélectionné",
        STR_HP to "PV",
        STR_MP to "PM",
        STR_IMG to "image",
        STR_SMALL_IMG to "img",
        STR_WARNING to "attention !",
        ST_SCENE_ALREADY_EXISTS to "Une scène avec le même nom existe déjà.",
        ST_ELEMENT_ALREADY_EXISTS to "Un élément avec ce nom existe déjà.",
        STR_CRITICAL_ERROR to "erreur critique",
        ST_ERROR_LOADING_DATABASE to """
            Une erreur s'est produite lors du chargement de la base de données. Impossible de lancer Olebo.
            Si l'erreur persiste, vous pouvez essayer de réinitialiser la base de données.
            Attention : Cette opération effacera toute vos données définitivement.
        """.trimIndent(),
        STR_ERROR to "erreur",
        ST_NEW_VERSION_AVAILABLE to "Une mise à jour de Olebo est disponible. Voulez-vous l'installer ?",
        STR_UPDATE_AVAILABLE to "mise à jour disponible",
        STR_YES to "oui",
        STR_NO to "non",
        STR_PREPARE_UPDATE to "préparation de la mise à jour",
        ST_UPDATE_OLEBO_RESTART to "Olebo va redémarrer une fois que la mise à jour sera installée.",
        ST_NEVER_ASK_UPDATE to "Non, ne plus demander pour cette version",
        ST_ERROR_ACT_NOT_EXISTS to "Erreur. Ce scénario ne semble pas exister.",
        ST_UNKNOWN_DATABASE_VERSION to "Erreur de base de données ! Version de la base inconnue.",
        STR_TRANSPARENT_POINTER to "pointeur transparent",
        STR_WHITE_POINTER to "pointeur blanc",
        STR_BLUE_POINTER to "pointeur bleu",
        STR_GREEN_POINTER to "pointeur vert",
        ST_CHANGE_VISIBILITY to "Modifier la visiblité de l'élément",
        ST_CHANGE_VISIBILITY_PLR to "Modifier la visiblité des éléments",
        STR_RESIZE_ELEMENT to "redimensionner un élément",
        STR_RESIZE_ELEMENT_PLR to "redimensionner des éléments",
        STR_MOVE_ELEMENT to "déplacer un élément",
        STR_MOVE_ELEMENTS to "déplacer les éléments",
        STR_OBJECT to "objet",
        STR_OPTIONS to "options",
        STR_GENERAL to "général",
        STR_SOFTWARE_LANGUAGE_LABEL to "langue :",
        STR_LOOK_AND_FEEL to "apparence",
        STR_CURSOR_COLOR_LABEL to "couleur du curseur :",
        ST_LANGUAGE_CHANGE_ON_RESTART to "Le changement de langue sera effectif au prochain redémarrage de Olebo.",
        STR_SAVE to "enregistrer",
        STR_RESTORE_DEFAULTS_OPTIONS to "rétablir les paramètres par défauts",
        STR_CUSTOM_COLOR to "couleur personnalisée",
        STR_PURPLE to "violet",
        STR_WHITE_WITH_BLACK_BORDER to "blanc avec une bordure noire",
        STR_BLACK_WITH_WHITE_BORDER to "noir avec une bordure blanche",
        STR_SELECT_COLOR to "choisissez une couleur",
        STR_PLAYER_FRAME_OPENED to "ouvrir automatiquement la fenêtre joueurs",
        STR_DOUBLE_CLICK_OPEN_ACT to "double cliquez pour ouvir le scénario",
        STR_DEFAULT_ELEMENT_VISIBILITY to "éléments visibles pour les joueurs par défaut",
        STR_YELLOW to "jaune",
        STR_RED to "rouge",
        ST_OLEBO_IS_UPDATING to "Olebo est en train de se mettre à jour.",
        ST_NOT_TURN_OFF to "N'éteignez pas votre ordinateur.",
        ST_UPDATE_SUCCESS to "Olebo a bien été mis à jour.",
        ST_UPDATE_FAILED to "Impossible de mettre à jour Olebo.",
        ST_UPDATE_TRY_AGAIN to "Veuillez réessayer ultérieurement.",
        STR_CANCEL_UPDATE to "annuler la mise à jour",
        STR_LABEL_STATE to "affichage du label :",
        STR_LABEL to "label",
        STR_SIZE_COMBO_TOOLTIP to "change la taille de l'élément",
        STR_PRIORITY_COMBO_TOOLTIP to "change le niveau de prorité de l'élément",
        STR_LABEL_TOOLTIP to "ajoute un label personalisé à l'élément",
        STR_DB_VERSION_MISMATCH to "erreur d'initialisation de la base de données",
        ST_DB_VERSION_MISMATCH_MESSAGE to """
            La base de données sur cet ordinateur provient d'une version ultérieur de Olebo. Pour pouvoir charger ces données, veuillez mettre à jour l'application ou la réinitialiser.
            Si vous choississez de continuer sans mettre à jour, certaines données risques d'être perdues et certaines fonctionnalités pourront ne pas fonctionner.
        """.trimIndent(),
        STR_EXIT to "quitter",
        STR_UPDATE to "mettre à jour",
        STR_RESET to "réinitialiser",
        ST_CANCEL_WILL_ERASE_CHANGES to "Tous les changements vont être effacé. Voulez-vous continuer ?",
        STR_BACK to "retour",
        STR_LOADING to "chargement",
        ST_NAME_OF_BLUEPRINT_REQUIRED to "Le nom de l'élément est requis.",
        ST_IMG_BLUEPRINT_REQUIRED to "Un élément doit posséder une image.",
        ST_IMG_BLUEPRINT_NOT_EXIST_OR_INVALID to "L'image sélectionnée n'existe pas ou est invalide.",
        ST_INT1_OCCURRENCE_BLUEPRINT_TO_DELETE to """
            Cet élément est utilisé dans %s scènes.
            Êtes-vous sûr de le supprimer? Cette action est irréversible.
        """.trimIndent(),
        ST_OCCURRENCE_BLUEPRINT_TO_DELETE to """
            Cet élément est utilisé dans 1 scène.
            Êtes-vous sûr de le supprimer? Cette action est irréversible.
        """.trimIndent(),
        ST_DEFAULT_SETTINGS_RESTORED to "Les paramètres par défaut ont été restaurés avec succès.",
        STR_INFO to "information",
        STR_BLACK to "noir",
        STR_LABEL_COLOR to "couleur du label :",
        STR_LABEL_DISABLED to "label désactivé",
        STR_LABEL_VISIBLE to "label visible sur les deux fenêtres",
        STR_LABEL_VISIBLE_FOR_MASTER to "label visible pour le maître du jeu",
        STR_LABEL_HIDDEN to "label caché",
        ST_ENTER_SCENE_NAME to "Entrez un nom de scène",
        ST_CONFIRM_DELETE_ACT to "Êtes-vous sûr de vouloir supprimer ce scénario ? Cette action est irréversible.",
        STR_DELETE_ACT to "supprimer scénario",
        STR_INSERT_ACT_NAME to "insérez un nom de scénario",
        ST_ACT_WITHOUT_NAME to "Un scénario doit avoir un nom.",
        STR_ADD_ELEMENT to "ajouter un élément",
        ST_STR1_DM_WINDOW_NAME to "Olebo - Fenêtre MJ - %s",
        STR_SIZE to "taille :",
        STR_PRIORITY to "plan :",
        STR_CLOSE to "fermer",
        STR_DOWNLOAD_UPDATE to "téléchargement de la mise à jour",
        STR_DOWNLOAD to "téléchargement :",
        ST_OLEBO_SEARCH_FOR_UPDATE to "Olebo recherche les mises à jour",
        STR_SELECT_DOWN to "élément précédent",
        STR_SELECT_UP to "élément suivant",
        STR_SELECT_ALL to "tout sélectionner",
        STR_CONFIRM_CREATE_SCENE to "sauvegarder la scène",
        STR_CONFIRM_EDIT_SCENE to "sauvegarder la scène",
        STR_CONFIRM_CREATE_ACT to "sauvegarder le scénario",
        STR_CONFIRM_EDIT_ACT to "sauvegarder le scénario",
        STR_OPEN_ACT_TOOLTIP to "ouvrir le scénario",
        STR_EDIT_ACT_TOOLTIP to "éditer le scenario",
        STR_EDIT_SCENE_TOOLTIP to "éditer la scène",
        STR_DELETE_SCENE_TOOLTIP to "supprimer la scène",
        STR_CONTINUE to "continuer",
        STR_VERSION_CODE to "code de version :",
        STR_NO_ACT to "aucun scénario",
        STR_NO_SCENE to "aucune scène",
        ST_DEVELOPED_BY to "Développé par Tristan BOULESTEIX et Vincent ONFRAY.",
        ST_SHOULD_OPEN_PLAYER_FRAME_FULL_SCREEN to """
            Ouvrir la fenêtre joueur en plein écran s'il y a un deuxième écran.
            (Cette fonctionnalité peut-être désactivée pour les écrans avec différentes résolutions / ratios)
        """.trimIndent(),
        ST_OLEBO_WEB_EXPLANATION to """
            ShareScene vous permet de partager la vue joueur directement sur internet en temps réel.
            Les joueurs doivent simplement posséder un navigateur web et une connection internet.
            Veuillez noter que les images des tokens seront compressées et pourront perdre un peu en qualité.
            
            Pour commencer, appuyez sur le bouton à droite puis partagez l'adresse internet fournie aux joueurs.
        """.trimIndent(),
        STR_START_OLEBO_WEB to "commencer et se connecter",
        STR_LOGIN_OLEBO_WEB to "connexion...",
        STR_ERROR_LOGIN_TO_OLEBO_WEB to "la connexion a échoué - merci de vérifier votre connection internet",
        STR_EDIT_TAB to "Édition",
        STR_OLEBO_WEB_TAB to "ShareScene (Olebo Web)",
    )
}