package viewModel

import model.dao.DAO
import model.element.Blueprint
import model.element.Type
import model.utils.saveImg
import view.editor.elements.BlueprintEditorDialog
import view.utils.showPopup
import viewModel.pattern.observer.Action
import viewModel.pattern.observer.Observable
import viewModel.pattern.observer.Observer
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
                if (selectedFile.exists()) {
                    File(Blueprint[id].sprite).delete()
                    Blueprint[id].sprite = saveImg(selectedFile.absolutePath)
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

    fun createBlueprint(@Suppress("UNUSED_PARAMETER") id: Int?) {
        transaction(DAO.database) {
            BlueprintEditorDialog(type).showDialog()?.let { map ->
                Blueprint.new {
                    this.type = this@BlueprintManager.type.type
                    this.name = map.name
                    if (this@BlueprintManager.type != Type.OBJECT) {
                        this.HP = map.life!!
                        this.MP = map.mana!!
                    }
                    this.sprite = saveImg(map.img)
                }
            }
        }

        notifyObserver(Action.REFRESH)
    }
}

/**
 * All informations from a blueprint stored in a class
 */
data class BlueprintData(val name: String, val img: String, val mana: Int? = null, val life: Int? = null, val id: Int? = null)