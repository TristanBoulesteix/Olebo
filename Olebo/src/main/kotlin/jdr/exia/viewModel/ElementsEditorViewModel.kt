package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.localization.ST_ELEMENT_ALREADY_EXISTS
import jdr.exia.localization.StringLocale
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Type
import jdr.exia.model.element.isValid
import jdr.exia.model.tools.saveImgAndGetPath
import jdr.exia.model.utils.Result
import jdr.exia.model.utils.assignIfDifferent
import jdr.exia.model.utils.isCharacter
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class ElementsEditorViewModel(private val type: Type) {
    private var currentEditPosition by mutableStateOf(-1)

    var blueprints by mutableStateOf(transaction { Blueprint.all().filter { it.type.typeElement == type } })

    val currentEditBlueprint
        get() = blueprints.getOrNull(currentEditPosition)

    var blueprintInCreation by mutableStateOf(null as Blueprint.BlueprintData?)
        private set

    fun onUpdateBlueprintInCreation(data: Blueprint.BlueprintData) {
        blueprintInCreation = data
    }

    fun onEditItemSelected(blueprintData: Blueprint) {
        currentEditPosition = blueprints.indexOf(blueprintData)
    }

    fun onRemoveBlueprint(blueprint: Blueprint) {
        onEditDone()
        blueprint.remove()
    }

    fun onEditConfirmed(data: Blueprint.BlueprintData) = transaction {
        val blueprint = currentEditBlueprint ?: return@transaction Result.Failure

        if (data.isValid() && blueprint.id == data.id) {
            if (blueprintWithNameExist(data.name, data.id))
                return@transaction Result.Failure(StringLocale[ST_ELEMENT_ALREADY_EXISTS])

            blueprint.apply {
                if (data.name.isNotBlank()) {
                    ::name.assignIfDifferent(data.name)
                }

                if (sprite != data.img.path) {
                    sprite = data.img.saveImgAndGetPath()
                }

                if (isCharacter()) {
                    ::HP.assignIfDifferent(data.life ?: HP)
                    ::MP.assignIfDifferent(data.mana ?: MP)
                }
            }

            onEditDone()

            Result.Success
        } else Result.Failure
    }


    fun startBlueprintCreation() {
        blueprintInCreation =
            Blueprint.BlueprintData.let { if (type == Type.OBJECT) it.defaultObject() else it.defaultCharacter() }
    }

    fun cancelBluprintCreation() {
        blueprintInCreation = null
    }

    fun onEditDone() {
        currentEditPosition = -1
    }

    fun onSubmitBlueprint(): Result {
        val blueprint = blueprintInCreation ?: return Result.Failure

        if (!blueprint.isValid())
            return Result.Failure

        return if (transaction { !blueprintWithNameExist(blueprint.name) }) {
            blueprint.create()
            Result.Success
        } else Result.Failure
    }

    private fun blueprintWithNameExist(name: String, excludedId: EntityID<Int>? = null) =
        blueprints.filter { it.type.typeElement == type && (excludedId == null || it.id != excludedId) }
            .any { it.name == name }

    private fun Blueprint.remove() = transaction {
        delete()
        blueprints = blueprints.toMutableList().also {
            it -= this@remove
        }
    }

    private fun (Blueprint.BlueprintData).create() = transaction {
        val blueprint = Blueprint.new {
            this.type = this@create.type.type
            this.name = this@create.name
            if (this@create.type != Type.OBJECT) {
                this.HP = this@create.life!!
                this.MP = this@create.mana!!
            }

            this.sprite = this@create.img.saveImgAndGetPath()
        }

        blueprints = blueprints.toMutableList().also {
            it += blueprint
        }
    }
}