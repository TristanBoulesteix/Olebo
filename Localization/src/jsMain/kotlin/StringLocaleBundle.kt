@file:Suppress("CanSealedSubClassBeObject", "ClassName", "unused")

package jdr.exia.localization

/**
 * Default strings (English strings)
 */
internal actual class StringLocaleBundle : StringLocale() {
    actual override val contents = mapOf(
        STR_SESSION_CODE to "session code:",
        STR_PLAYER_NAME to "player name:",
        STR_LOGIN to "login",
        STR_START to "start",
        ST_INVALID_SESSION_PARAM to "The session code provided is invalid or the session does not exist.",
    )
}

/**
 * French strings
 */
@JsName("StringLocaleBundleFr")
internal actual class StringLocaleBundle_fr : StringLocale() {
    actual override val contents = mapOf(
        STR_SESSION_CODE to "code de session :",
        STR_PLAYER_NAME to "nom du joueur :",
        STR_LOGIN to "connection",
        STR_START to "démarrer",
        ST_INVALID_SESSION_PARAM to "Le code de session entré est invalide ou la session n'existe pas.",
    )
}