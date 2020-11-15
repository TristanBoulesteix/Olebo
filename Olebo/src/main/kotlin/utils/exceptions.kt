package utils

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

class DatabaseException(e: Exception) : Exception(e) {
    init {
        JOptionPane.showMessageDialog(
                FocusManager.getCurrentManager().activeWindow,
                "Une erreur s'est produite lors du chargement de la base de données. Impossible de lancer Olebo.",
                "Erreur critique",
                JOptionPane.ERROR_MESSAGE
        )
    }
}