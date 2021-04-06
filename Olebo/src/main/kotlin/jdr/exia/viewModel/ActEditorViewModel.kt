package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.model.act.Act
import jdr.exia.model.act.isValid
import jdr.exia.model.act.isValidAndEqualTo
import jdr.exia.model.utils.Image
import org.jetbrains.exposed.sql.emptySized
import org.jetbrains.exposed.sql.mapLazy
import org.jetbrains.exposed.sql.transactions.transaction

class ActEditorViewModel(private val act: Act?) {
    private var currentEditPosition by mutableStateOf(-1)

    var scenes by transaction {
        mutableStateOf((act?.scenes ?: emptySized()).mapLazy {
            Act.SceneData(it.name, Image(it.background), it.id.value)
        }.toList())
    }
        private set

    val currentEditScene
        get() = scenes.getOrNull(currentEditPosition)

    fun onAddScene(sceneData: Act.SceneData) = if (sceneData.isValid() && !sceneWithNameExist(sceneData.name)) {
        scenes = scenes + listOf(sceneData)
        true
    } else false

    fun onEditItemSelected(sceneData: Act.SceneData) {
        currentEditPosition = scenes.indexOf(sceneData)
    }

    fun onEditConfirmed(sceneData: Act.SceneData): Boolean {
        if (currentEditScene isValidAndEqualTo sceneData && !sceneWithNameExist(sceneData.name, sceneData.id)) {
            scenes = scenes.toMutableList().also {
                it[currentEditPosition] = sceneData
            }

            onEditDone()

            return true
        }

        return false
    }

    fun onEditDone() {
        currentEditPosition = -1
    }

    private fun sceneWithNameExist(name: String, excludedId: Int? = null) =
        scenes.filter { excludedId == null || it.id != excludedId }.any { it.name == name }
}