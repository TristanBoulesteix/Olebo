package jdr.exia.model.utils

import javax.swing.FocusManager
import javax.swing.JOptionPane

class MessageException(message: String) : Exception(message) {
    init {
        JOptionPane.showMessageDialog(FocusManager.getCurrentManager().activeWindow, message, "Error", JOptionPane.ERROR_MESSAGE)
    }
}