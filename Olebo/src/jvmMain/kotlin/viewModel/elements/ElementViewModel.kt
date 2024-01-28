package jdr.exia.viewModel.elements

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.TypeElement
import jdr.exia.viewModel.holder.BlueprintData
import jdr.exia.viewModel.holder.isDefault
import jdr.exia.viewModel.holder.toBlueprintData
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

@Stable
class ElementViewModel(type: TypeElement) {
    val currentScrollState = LazyListState()

    var currentEditPosition by mutableIntStateOf(-1)

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