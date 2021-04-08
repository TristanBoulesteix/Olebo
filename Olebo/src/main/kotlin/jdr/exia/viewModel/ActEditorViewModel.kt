package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.localization.ST_ACT_ALREADY_EXISTS
import jdr.exia.localization.ST_ACT_WITHOUT_NAME
import jdr.exia.localization.ST_ACT_WITHOUT_SCENE
import jdr.exia.localization.StringLocale
import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.act.isValid
import jdr.exia.model.act.isValidAndEqualTo
import jdr.exia.model.dao.saveImgAndGetPath
import jdr.exia.model.tools.Image
import jdr.exia.model.utils.Result
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.emptySized
import org.jetbrains.exposed.sql.mapLazy
import org.jetbrains.exposed.sql.transactions.transaction

class ActEditorViewModel(private val act: Act?) {
    private var currentEditPosition by mutableStateOf(-1)

    var actName by mutableStateOf(act?.name ?: "")

    var scenes by transaction {
        mutableStateOf((act?.scenes ?: emptySized()).mapLazy {
            Act.SceneData(it.name, Image(it.background), it.id)
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

    fun onRemoveScene(sceneData: Act.SceneData) {
        scenes = scenes.toMutableList().also {
            it.remove(sceneData)
        }

        onEditDone()
    }

    fun onEditDone() {
        currentEditPosition = -1
    }

    fun submitAct(): Result {
        if (scenes.isEmpty())
            return Result.Failure(StringLocale[ST_ACT_WITHOUT_SCENE])
        if (actName.isBlank()) {
            if (act == null) {
                return Result.Failure(StringLocale[ST_ACT_WITHOUT_NAME])
            } else {
                actName = act.name
            }
        }

        if(transaction {(act != null && Act.all().filterNot { it.id == act.id }.any { it.name == actName } ) || (act == null && Act.all().any { it.name == actName })})
            return Result.Failure(StringLocale[ST_ACT_ALREADY_EXISTS])

        val act = transaction { act?.also { it.name = actName } ?: Act.new { this.name = actName } }

        val idList = scenes.mapNotNull { it.id }

        transaction {
            act.scenes.filter { it.id !in idList }.forEach(Scene::delete)

            scenes.forEach {
                if (it.id != null) with(Scene[it.id]) {
                    this.name = it.name
                    this.background = it.img.saveImgAndGetPath()
                } else Scene.new {
                    this.name = it.name
                    this.background = it.img.saveImgAndGetPath()
                    this.idAct = act.id.value
                }
            }
        }

        return Result.Success
    }


    private fun sceneWithNameExist(name: String, excludedId: EntityID<Int>? = null) =
        scenes.filter { excludedId == null || it.id != excludedId }.any { it.name == name }
}