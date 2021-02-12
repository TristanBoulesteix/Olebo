package jdr.exia.viewModel

import jdr.exia.localization.*
import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.dao.saveImg
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.Type
import jdr.exia.view.frames.home.editor.BlueprintEditorDialog
import jdr.exia.view.utils.showConfirmMessage
import jdr.exia.view.utils.showPopup
import jdr.exia.viewModel.observer.Action
import jdr.exia.viewModel.observer.Observable
import jdr.exia.viewModel.observer.Observer
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Component
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class BlueprintManager(private val homeManager: HomeManager) : Observable {
    val elements
        get() = DAO.getElementsWithType(type)

    var type = Type.OBJECT
        set(value) {
            field = value
            notifyObserver(Action.Reload)
        }

    override var observer: Observer?
        get() = homeManager.observer
        set(value) {
            homeManager.observer = value
        }

    fun getBlueprintWithId(id: Int) = transaction {
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

        transaction {
            if (isNameValid()) {
                getBlueprintWithId(id).name = name
            } else {
                showPopup(Strings[ST_ELEMENT_ALREADY_EXISTS])
            }
        }

        notifyObserver(Action.Reload)
    }

    fun deleteElement(id: Int) {
        transaction {
            Blueprint[id].let { blueprint ->
                val countUsage = Element.find { InstanceTable.idBlueprint eq blueprint.id.value }.count()

                if (countUsage > 0) {
                    showConfirmMessage(
                        null,
                        if (countUsage == 1L)
                            Strings[ST_OCCURENCE_BLUEPRINT_TO_DELETE]
                        else
                            Strings[ST_INT1_OCCURENCE_BLUEPRINT_TO_DELETE, countUsage],
                        Strings[STR_WARNING],
                        okAction = blueprint::delete
                    )
                } else {
                    blueprint.delete()
                }
            }
        }

        notifyObserver(Action.Reload)
    }

    fun updateIcon(id: Int) {
        transaction {
            val file = JFileChooser().apply {
                this.currentDirectory = File(System.getProperty("user.home"))
                this.addChoosableFileFilter(
                    FileNameExtensionFilter(Strings[STR_IMG], *ImageIO.getReaderFileSuffixes())
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

        notifyObserver(Action.Reload)
    }

    fun saveMana(id: Int, text: String) {
        transaction {
            Blueprint[id].MP = text.toIntOrNull() ?: 0
        }
    }

    fun saveLife(id: Int, text: String) {
        transaction {
            Blueprint[id].HP = text.toIntOrNull() ?: 0
        }
    }

    fun createBlueprint(@Suppress("UNUSED_PARAMETER") id: Int?) {
        transaction {
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

        notifyObserver(Action.Reload)
    }
}

/**
 * All informations from a blueprint stored in a class
 */
data class BlueprintData(
    val name: String,
    val img: String,
    val mana: Int? = null,
    val life: Int? = null,
    val id: Int? = null
)