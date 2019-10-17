package jdr.exia.controller

import jdr.exia.model.dao.DAO
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Type
import jdr.exia.pattern.observer.Action
import jdr.exia.pattern.observer.Observable
import jdr.exia.pattern.observer.Observer
import jdr.exia.view.editor.elements.BlueprintEditorDialog
import jdr.exia.view.utils.showPopup
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Component
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class BlueprintManager : Observable {
    val elements
        get() = DAO.getElementsWithType(type)

    var type = Type.OBJECT
        set(value) {
            field = value
            notifyObserver(Action.REFRESH)
        }

    override var observer: Observer? = null

    fun getBlueprintWithId(id: Int) = transaction(DAO.database) {
        elements.find { it.id.value == id }!!
    }

    fun updateName(id: Int, name: String) {
        fun isNameValid(): Boolean {
            elements.forEach {
                if ((it.id.value != id && it.name == name) || name.isBlank()) {
                    return false
                }
            }
            return true
        }

        transaction(DAO.database) {
            if (isNameValid()) {
                getBlueprintWithId(id).name = name
            } else {
                showPopup("Désolé. Un objet avec le même nom existe déjà")
            }
        }

        notifyObserver(Action.REFRESH)
    }

    fun deleteElement(id: Int) {
        transaction(DAO.database) {
            Blueprint[id].delete()
        }

        notifyObserver(Action.REFRESH)
    }

    fun updateIcon(id: Int) {
        transaction(DAO.database) {
            val file = JFileChooser().apply {
                this.currentDirectory = File(System.getProperty("user.home"))
                this.addChoosableFileFilter(
                    FileNameExtensionFilter("Images", *ImageIO.getReaderFileSuffixes())
                )
                this.isAcceptAllFileFilterUsed = false
            }
            val result = file.showSaveDialog(observer as? Component)

            if (result == JFileChooser.APPROVE_OPTION) {
                val selectedFile = file.selectedFile
                if(selectedFile.exists()) {
                    File(Blueprint[id].sprite).delete()
                    Blueprint[id].sprite = selectedFile.absolutePath
                }
            }
        }

        notifyObserver(Action.REFRESH)
    }

    fun saveMana(id: Int, text: String) {
        transaction(DAO.database) {
            Blueprint[id].MP = text.toInt()
        }
    }

    fun saveLife(id: Int, text: String) {
        transaction(DAO.database) {
            Blueprint[id].HP = text.toInt()
        }
    }

    fun createBlueprint(@Suppress("UNUSED_PARAMETER") id: Int) {
        BlueprintEditorDialog(type).showDialog()?.let {

        }
    }
}