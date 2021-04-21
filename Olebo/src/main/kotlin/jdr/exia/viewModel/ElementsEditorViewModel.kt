package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    fun onEditItemSelected(blueprintData: Blueprint) {
        currentEditPosition = blueprints.indexOf(blueprintData)
    }

    fun onRemoveBlueprint(blueprint: Blueprint) {
        onEditDone()
        blueprint.remove()
    }

    fun onEditConfirmed(data: Blueprint.BlueprintData) = transaction {
        val blueprint = currentEditBlueprint ?: return@transaction Result.Failure()

        if (data.isValid() && blueprint.id == data.id) {
            blueprint.apply {
                if (data.name.isNotBlank()) {
                    ::name.assignIfDifferent(data.name)
                }

                if (sprite != data.img.path) {
                    sprite = data.img.saveImgAndGetPath()
                }

                if (isCharacter()) {
                    ::HP.assignIfDifferent(data.life ?: 0)
                    ::MP.assignIfDifferent(data.mana ?: 0)
                }
            }

            onEditDone()

            Result.Success
        } else Result.Failure()
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

    fun onSubmitBlueprint() = true

    private fun blueprintWithNameExist(name: String, excludedId: EntityID<Int>? = null) =
        blueprints.filter { it.type.typeElement == type && (excludedId == null || it.id != excludedId) }
            .any { it.name == name }

    private fun Blueprint.remove() = transaction {
        delete()
        blueprints = blueprints.toMutableList().also {
            it -= this@remove
        }
    }
}