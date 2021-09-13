package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.localization.*
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.TypeElement
import jdr.exia.model.element.isValid
import jdr.exia.model.tools.*
import jdr.exia.model.type.saveImgAndGetPath
import jdr.exia.view.tools.showConfirmMessage
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class ElementsEditorViewModel(private val type: TypeElement) {
    private var currentEditPosition by mutableStateOf(-1)

    var blueprints by mutableStateOf(transaction { Blueprint.all().filter { it.type == type } })

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

    fun onEditConfirmed(data: Blueprint.BlueprintData): SimpleResult = transaction {
        val blueprint = currentEditBlueprint ?: return@transaction Result.failure

        if (data.isValid() && blueprint.id == data.id) {
            if (blueprintWithNameExist(data.name, data.id))
                return@transaction Result.failure(IllegalStateException(StringLocale[ST_ELEMENT_ALREADY_EXISTS]))

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

            Result.success
        } else Result.failure
    }

    fun startBlueprintCreation() {
        blueprintInCreation =
            Blueprint.BlueprintData.let { if (type == TypeElement.Object) it.defaultObject() else it.defaultCharacter(type) }
    }

    fun cancelBlueprintCreation() {
        blueprintInCreation = null
    }

    fun onEditDone() {
        currentEditPosition = -1
    }

    fun onSubmitBlueprint(): SimpleResult {
        val blueprint = blueprintInCreation ?: return Result.failure

        if (!blueprint.isValid())
            return Result.failure

        return if (transaction { !blueprintWithNameExist(blueprint.name) }) {
            blueprint.create()
            Result.success
        } else Result.failure
    }

    private fun blueprintWithNameExist(name: String, excludedId: EntityID<Int>? = null) =
        blueprints.filter { it.type == type && (excludedId == null || it.id != excludedId) }
            .any { it.name == name }

    private fun Blueprint.remove() = transaction {
        val countUsage =
            Element.find { InstanceTable.idBlueprint eq this@remove.id.value }.distinctBy { it.scene.id }.count()

        fun deleteAndClearState() {
            delete()
            blueprints = blueprints.toMutableList().also {
                it -= this@remove
            }
        }

        if (countUsage > 0) {
            showConfirmMessage(
                null,
                if (countUsage == 1)
                    StringLocale[ST_OCCURRENCE_BLUEPRINT_TO_DELETE]
                else
                    StringLocale[ST_INT1_OCCURRENCE_BLUEPRINT_TO_DELETE, countUsage],
                StringLocale[STR_WARNING],
                okAction = ::deleteAndClearState
            )
        } else {
            deleteAndClearState()
        }
    }

    private fun (Blueprint.BlueprintData).create() = transaction {
        val blueprint = Blueprint.new {
            this.type = this@create.type
            this.name = this@create.name
            if (this@create.type != TypeElement.Object) {
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