package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.model.act.Act
import org.jetbrains.exposed.sql.emptySized
import org.jetbrains.exposed.sql.mapLazy
import org.jetbrains.exposed.sql.transactions.transaction

class ActEditorViewModel(private val act: Act?) {
    private var currentEditPosition by mutableStateOf(-1)

    var scenes by transaction {
        mutableStateOf((act?.scenes ?: emptySized()).mapLazy {
            Act.SceneData(it.name, it.background, it.id.value)
        }.toList())
    }
        private set

    val currentEditScene
        get() = scenes.getOrNull(currentEditPosition)

    fun onEditItemSelected(sceneData: Act.SceneData) {
        currentEditPosition = scenes.indexOf(sceneData)
    }
}