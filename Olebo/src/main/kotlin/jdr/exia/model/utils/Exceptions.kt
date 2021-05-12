package jdr.exia.utils

import jdr.exia.localization.STR_CRITICAL_ERROR
import jdr.exia.localization.STR_ERROR
import jdr.exia.localization.ST_ERROR_LOADING_DATABASE
import jdr.exia.localization.StringLocale
import javax.swing.FocusManager
import javax.swing.JOptionPane
import kotlin.reflect.KClass

class MessageException(message: String) : Exception(message) {
    init {
        JOptionPane.showMessageDialog(
            FocusManager.getCurrentManager().activeWindow,
            message,
            StringLocale[STR_ERROR],
            JOptionPane.ERROR_MESSAGE
        )
    }
}

class CharacterException(kClass: KClass<*>, varName: String?) :
    Exception("Cet objet n'est pas un personnage !" + varName?.let { " Impossible d'accéder à la variable $varName dans ${kClass.qualifiedName}" })

class DatabaseException(e: Exception) : Exception(e) {
    init {
        JOptionPane.showMessageDialog(
            null,
            StringLocale[ST_ERROR_LOADING_DATABASE],
            StringLocale[STR_CRITICAL_ERROR],
            JOptionPane.ERROR_MESSAGE
        )
    }
}