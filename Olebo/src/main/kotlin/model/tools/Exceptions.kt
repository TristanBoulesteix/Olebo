package jdr.exia.model.tools

import jdr.exia.localization.*
import jdr.exia.model.dao.reset
import jdr.exia.model.dao.restart
import jdr.exia.view.tools.showConfirmMessage
import jdr.exia.view.tools.windowAncestor
import javax.swing.JButton
import javax.swing.JOptionPane
import kotlin.reflect.KClass
import kotlin.system.exitProcess

class CharacterException(kClass: KClass<*>, varName: String?) :
    Exception("Cet objet n'est pas un personnage !" + varName?.let { " Impossible d'accéder à la variable $varName dans ${kClass.qualifiedName}" })

class DatabaseException(e: Throwable) : Exception(e) {
    init {
        val reset = JButton(StringLocale[STR_RESET]).apply {
            this.addActionListener {
                windowAncestor?.dispose()
                showConfirmMessage(
                    windowAncestor,
                    StringLocale[ST_WARNING_CONFIG_RESET],
                    StringLocale[STR_RESET],
                    confirm = true
                ) {
                    reset()
                    restart()
                }
            }
        }

        val exit = JButton(StringLocale[STR_EXIT]).apply { this.addActionListener { exitProcess(100) } }

        JOptionPane.showOptionDialog(
            null,
            StringLocale[ST_ERROR_LOADING_DATABASE],
            StringLocale[STR_CRITICAL_ERROR],
            JOptionPane.NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            arrayOf(reset, exit),
            exit
        )

        exitProcess(100)
    }
}