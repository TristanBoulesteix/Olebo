package jdr.exia.viewModel

import androidx.compose.runtime.*
import jdr.exia.localization.*
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.TypeElement
import jdr.exia.model.element.isValid
import jdr.exia.model.tools.*
import jdr.exia.model.type.checkedImgPath
import jdr.exia.model.type.saveImgAndGetPath
import jdr.exia.model.type.toImgPath
import jdr.exia.view.tools.showConfirmMessage
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class ElementsEditorViewModel(InitialType: TypeElement) {
    private var currentEditPosition by mutableStateOf(-1)

    var currentType by mutableStateOf(InitialType)

    val blueprints by derivedStateOf {
        transaction {
            Blueprint.all().filter { it.type == currentType }
        }.toMutableStateList()
    }

    val currentEditBlueprint
        get() = blueprints.getOrNull(currentEditPosition)

    val blueprintsInCreation =
        TypeElement.values().map { it to null }.toMutableStateMap<TypeElement, Blueprint.BlueprintData?>()

    val hasBlueprintInCreationForCurrentType
        get() = blueprintsInCreation[currentType] != null

    fun onUpdateBlueprintInCreation(data: Blueprint.BlueprintData) {
        blueprintsInCreation[data.type] = data
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
                    val oldImg = sprite.toImgPath().checkedImgPath()?.toFile()
                    sprite = data.img.saveImgAndGetPath("blueprint")
                    oldImg?.delete()
                }

                if (isCharacter()) {
                    ::HP.assignIfDifferent(data.life ?: HP)
                    ::MP.assignIfDifferent(data.mana ?: MP)
                }
            }
            Result.success
        } else {
            Result.failure
        }
    }

    fun startBlueprintCreation() {
        blueprintsInCreation[currentType] =
            Blueprint.BlueprintData.let {
                if (currentType == TypeElement.Object) it.defaultObject() else it.defaultCharacter(
                    currentType
                )
            }
    }

    fun cancelBlueprintCreation() {
        blueprintsInCreation[currentType] = null
    }

    fun onEditDone() {
        currentEditPosition = -1
    }

    fun onSubmitBlueprint(): SimpleResult {
        val blueprint = blueprintsInCreation[currentType] ?: return Result.failure

        if (!blueprint.isValid())
            return Result.failure

        return if (transaction { !blueprintWithNameExist(blueprint.name) }) {
            blueprint.create()
            Result.success
        } else Result.failure
    }

    private fun blueprintWithNameExist(name: String, excludedId: EntityID<Int>? = null) =
        blueprints.filter { it.type == currentType && (excludedId == null || it.id != excludedId) }
            .any { it.name == name }

    private fun Blueprint.remove() = transaction {
        val countUsage =
            Element.find { InstanceTable.idBlueprint eq this@remove.id.value }.distinctBy { it.scene.id }.count()

        fun deleteAndClearState() {
            delete()
            blueprints -= this@remove
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

        blueprints += blueprint
    }
}