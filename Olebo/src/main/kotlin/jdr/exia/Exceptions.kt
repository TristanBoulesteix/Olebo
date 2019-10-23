package jdr.exia

import javax.swing.FocusManager
import javax.swing.JOptionPane
import kotlin.reflect.KClass

class MessageException(message: String) : Exception(message) {
    init {
        JOptionPane.showMessageDialog(
            FocusManager.getCurrentManager().activeWindow,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        )
    }
}

class CharacterException(kClass: KClass<*>, varName: String?) :
    Exception("Cet objet n'est pas un personnage !" + varName?.let { " Impossible d'accéder à la variable $varName dans ${kClass.qualifiedName}" })