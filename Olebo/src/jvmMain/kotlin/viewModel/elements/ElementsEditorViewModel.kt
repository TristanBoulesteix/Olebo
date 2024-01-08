package jdr.exia.viewModel.elements

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.dao.InstanceTable
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.Tag
import jdr.exia.model.element.TypeElement
import jdr.exia.model.tools.*
import jdr.exia.model.type.checkedImgPath
import jdr.exia.model.type.saveImgAndGetPath
import jdr.exia.model.type.toImgPath
import jdr.exia.view.tools.showConfirmMessage
import jdr.exia.viewModel.holder.BlueprintData
import jdr.exia.viewModel.holder.isValid
import jdr.exia.viewModel.tags.ElementTagHolder
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

@Stable
class ElementsEditorViewModel(initialAct: Act?, initialType: TypeElement) {
    private val typeViewModel = TypeElement.entries.associateWith(::ElementViewModel)

    val itemListScrollState
        get() = currentTypeViewModel.currentScrollState

    var currentType by settableMutableStateOf(initialType) { onEditDone() }

    var selectedAct by settableMutableStateOf(initialAct) { onEditDone() }

    private val currentTypeViewModel
        get() = typeViewModel.getOrElse(currentType) { ElementViewModel(currentType) }

    private val currentEditPosition by derivedStateOf { currentTypeViewModel.currentEditPosition }

    private val elementTagHolder = ElementTagHolder()

    val blueprints: List<BlueprintData> by derivedStateOf {
        val data = (currentTypeViewModel.createdData + currentTypeViewModel.data)

        data.takeIf { selectedAct == null } ?: data.filter { selectedAct in it.associatedActs }
    }

    val currentEditBlueprint
        get() = blueprints.getOrNull(currentEditPosition)

    val tagsAsString: Iterable<String> by elementTagHolder::tags

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
            if (currentType == TypeElement.Object) it.defaultObject(selectedAct.toSingletonList())
            else it.defaultCharacter(currentType, selectedAct.toSingletonList())
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

    /**
     * Save changes made by user on blueprints.
     * This method is invoked when the user click on the submit button on the blueprint edition screen.
     */
    fun saveChanges() {
        // Save tags changes
        val deletedTags = elementTagHolder.pushToDatabase()

        // Save blueprints
        typeViewModel.forEach { (_, viewModel) ->
            // Save newly created data
            viewModel.createdData.forEach { newData ->
                transaction {
                    Blueprint.new {
                        setData(newData, deletedTags)

                        sprite = newData.img.saveImgAndGetPath(suffix = "blueprint")
                    }
                }
            }

            // Update modified data
            viewModel.modifiedData.forEach modified@{ (id, isNotDeleted) ->
                if (isNotDeleted) {
                    val oldData = viewModel.data.find { it.id == id } ?: return@modified

                    transaction {
                        Blueprint[id].apply {
                            setData(oldData, deletedTags)

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
    }

    fun createTags(tags: List<String>) = elementTagHolder.createTags(tags)

    fun deleteTags(tags: List<String>) = elementTagHolder.deleteTags(tags)

    private fun Blueprint.setData(data: BlueprintData, deletedTags: Set<String>) {
        type = data.type
        name = data.name.trim()

        if (data.type != TypeElement.Object) {
            healthPoints = data.life!!
            manaPoint = data.mana!!
        }

        tags = (data.tags - deletedTags).map { Tag[it] }.toSizedCollection()
        associatedAct = (associatedAct + data.associatedActs.toSet()).toSizedCollection()
    }
}