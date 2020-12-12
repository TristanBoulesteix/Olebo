package utils

import model.dao.localization.STR_CRITICAL_ERROR
import model.dao.localization.STR_ERROR
import model.dao.localization.ST_ERROR_LOADING_DATABASE
import model.dao.localization.Strings
import javax.swing.FocusManager
import javax.swing.JOptionPane
import kotlin.reflect.KClass

class MessageException(message: String) : Exception(message) {
    init {
        JOptionPane.showMessageDialog(
            FocusManager.getCurrentManager().activeWindow,
            message,
            Strings[STR_ERROR],
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
            Strings[ST_ERROR_LOADING_DATABASE],
            Strings[STR_CRITICAL_ERROR],
            JOptionPane.ERROR_MESSAGE
        )
    }
}