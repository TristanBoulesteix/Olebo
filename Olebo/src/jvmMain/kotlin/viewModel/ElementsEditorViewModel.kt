package jdr.exia.viewModel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import jdr.exia.localization.*
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.TypeElement
import jdr.exia.model.tools.SimpleResult
import jdr.exia.model.tools.failure
import jdr.exia.model.tools.success
import jdr.exia.model.type.checkedImgPath
import jdr.exia.model.type.saveImgAndGetPath
import jdr.exia.model.type.toImgPath
import jdr.exia.view.tools.showConfirmMessage
import jdr.exia.viewModel.data.BlueprintData
import jdr.exia.viewModel.data.isDefault
import jdr.exia.viewModel.data.isValid
import jdr.exia.viewModel.data.toBlueprintData
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class ElementsEditorViewModel(initialType: TypeElement) {
    private val typeViewModel = TypeElement.values().associateWith { ElementViewModel(it) }

    val itemListScrollState
        get() = currentTypeViewModel.currentScrollState

    var currentType by mutableStateOf(initialType)

    private val currentTypeViewModel
        get() = typeViewModel.getOrElse(currentType) { ElementViewModel(currentType) }

    private val currentEditPosition by derivedStateOf { currentTypeViewModel.currentEditPosition }

    val blueprints: List<BlueprintData> by derivedStateOf { currentTypeViewModel.createdData + currentTypeViewModel.data }

    val currentEditBlueprint
        get() = blueprints.getOrNull(currentEditPosition)

    fun onEditItemSelected(blueprint: BlueprintData) {
        currentTypeViewModel.clearDefaultNew()
        currentTypeViewModel.currentEditPosition = blueprints.indexOf(blueprint)
    }

    fun onRemoveBlueprint(data: BlueprintData) {
        data.remove()
        onEditDone()
    }

    fun onEditConfirmed(data: BlueprintData): SimpleResult = transaction {
        val blueprint = currentEditBlueprint ?: return@transaction Result.failure

        if (blueprint.id != data.id)
            return@transaction Result.failure

        if (blueprintWithNameExist(data.name, data.id))
            return@transaction Result.failure(StringLocale[ST_ELEMENT_ALREADY_EXISTS])

        if (!data.isValid())
            return@transaction Result.failure(StringLocale[if (data.id == null) ST_NO_SPRITE_FOR_NEW_BLUEPRINT else ST_INVALID_SPRITE_FOR_BLUEPRINT])

        if (data.id == null) {
            val list = currentTypeViewModel.createdData
            list[list.indexOfFirst { it === blueprint }] = data
        } else {
            currentTypeViewModel.updateExistingData(data)
        }

        Result.success
    }

    fun startBlueprintCreation() {
        val createdBlueprint = BlueprintData.let {
            if (currentType == TypeElement.Object) it.defaultObject() else it.defaultCharacter(currentType)
        }

        currentTypeViewModel.createdData.add(0, createdBlueprint)
        currentTypeViewModel.currentEditPosition = 0
    }

    fun onEditDone() {
        currentTypeViewModel.clearDefaultNew()
        currentTypeViewModel.currentEditPosition = -1
    }

    private fun blueprintWithNameExist(name: String, excludedId: EntityID<Int>? = null) =
        blueprints.filter { it.type == currentType && (excludedId == null || it.id != excludedId) }
            .any { it.name == name }

    private fun BlueprintData.remove() {
        val countUsage =
            if (id == null) 0 else transaction {
                Element.find { InstanceTable.idBlueprint eq this@remove.id.value }
                    .distinctBy { it.scene.id }.count()
            }

        val deleteData = {
            if (id != null) {
                currentTypeViewModel.deleteExistingData(id)
            } else {
                currentTypeViewModel.deleteCreatedData(this)
            }
        }

        if (countUsage > 0) {
            showConfirmMessage(
                parent = null,
                message = if (countUsage == 1)
                    StringLocale[ST_OCCURRENCE_BLUEPRINT_TO_DELETE]
                else
                    StringLocale[ST_INT1_OCCURRENCE_BLUEPRINT_TO_DELETE, countUsage],
                title = StringLocale[STR_WARNING],
                okAction = deleteData
            )
        } else deleteData()
    }

    fun saveChanges() = typeViewModel.forEach { (_, viewModel) ->
        viewModel.createdData.forEach { newData ->
            transaction {
                Blueprint.new {
                    type = newData.type
                    name = newData.name

                    if (newData.type != TypeElement.Object) {
                        HP = newData.life!!
                        MP = newData.mana!!
                    }

                    sprite = newData.img.saveImgAndGetPath()
                }
            }
        }

        viewModel.modifiedData.forEach modified@{ (id, isNotDeleted) ->
            if (isNotDeleted) {
                val oldData = viewModel.data.find { it.id == id } ?: return@modified

                transaction {
                    Blueprint[id].apply {
                        type = oldData.type
                        name = oldData.name

                        if (oldData.type != TypeElement.Object) {
                            HP = oldData.life!!
                            MP = oldData.mana!!
                        }

                        if (oldData.img.path != sprite) {
                            val oldImg = sprite.toImgPath().checkedImgPath()?.toFile()
                            sprite = oldData.img.saveImgAndGetPath(suffix = "blueprint")
                            oldImg?.delete()
                        }
                    }
                }
            } else {
                transaction { Blueprint[id].delete() }
            }
        }
    }

    private class ElementViewModel(type: TypeElement) {
        val currentScrollState = LazyListState()

        var currentEditPosition by mutableStateOf(-1)

        val data by lazy {
            transaction {
                Blueprint.all().filter { it.type == type }.map { it.toBlueprintData() }
            }.toMutableStateList()
        }

        val createdData: MutableList<BlueprintData> = mutableStateListOf()

        val modifiedData = mutableMapOf<EntityID<Int>, Boolean>()

        fun clearDefaultNew() = createdData.removeAll(BlueprintData::isDefault)

        fun deleteExistingData(idOfEntityToDelete: EntityID<Int>) {
            modifiedData[idOfEntityToDelete] = false
            data.removeIf { it.id == idOfEntityToDelete }
        }

        fun deleteCreatedData(dataToDelete: BlueprintData) {
            if (dataToDelete.isDefault()) {
                clearDefaultNew()
            } else {
                createdData.remove(dataToDelete)
            }
        }

        fun updateExistingData(dataToUpdate: BlueprintData) {
            require(dataToUpdate.id != null)

            data[data.indexOfFirst { it.id == dataToUpdate.id }] = dataToUpdate
            modifiedData[dataToUpdate.id] = true
        }
    }
}