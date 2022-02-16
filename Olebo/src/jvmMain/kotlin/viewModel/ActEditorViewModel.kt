package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.localization.*
import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.act.data.SceneData
import jdr.exia.model.act.data.isValid
import jdr.exia.model.dao.SceneTable
import jdr.exia.model.tools.SimpleResult
import jdr.exia.model.tools.success
import jdr.exia.model.type.Image
import jdr.exia.model.type.checkedImgPath
import jdr.exia.model.type.saveImgAndGetPath
import jdr.exia.model.type.toImgPath
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.emptySized
import org.jetbrains.exposed.sql.mapLazy
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.io.path.deleteIfExists

class ActEditorViewModel(private val act: Act?) {
    private var currentEditPosition by mutableStateOf(-1)

    var actName by mutableStateOf(act?.name ?: "")

    var errorMessage by mutableStateOf("")

    var scenes by transaction {
        mutableStateOf(
            (act?.scenes ?: emptySized()).mapLazy {
                SceneData(it.name, Image(it.background), it.id)
            }.toList()
        )
    }
        private set

    val currentEditScene
        get() = scenes.getOrNull(currentEditPosition)

    fun onAddScene(sceneData: SceneData): SimpleResult =
        if (sceneData.isValid() && !sceneWithNameExist(sceneData.name)) {
            scenes = scenes + listOf(sceneData.copy(name = sceneData.name.trimEnd()))
            Result.success
        } else Result.failure(IllegalArgumentException("Scene data invalid"))

    fun onEditItemSelected(sceneData: SceneData) {
        currentEditPosition = scenes.indexOf(sceneData)
    }

    fun onEditConfirmed(sceneData: SceneData): Boolean {
        if (currentEditScene?.id == sceneData.id && sceneData.isValid() && !sceneWithNameExist(
                sceneData.name,
                sceneData.id
            )
        ) {
            scenes = scenes.toMutableList().also {
                it[currentEditPosition] = sceneData.copy(name = sceneData.name.trimEnd())
            }

            onEditDone()

            return true
        }

        return false
    }

    fun onRemoveScene(sceneData: SceneData) {
        scenes = scenes.toMutableList().also {
            it.remove(sceneData)
        }

        onEditDone()
    }

    fun onEditDone() {
        currentEditPosition = -1
    }

    fun submitAct(): SimpleResult {
        if (scenes.isEmpty())
            return Result.failure(IllegalStateException(StringLocale[ST_ACT_WITHOUT_SCENE]))

        if (actName.isBlank()) {
            if (act == null) {
                return Result.failure(IllegalStateException(StringLocale[ST_ACT_WITHOUT_NAME]))
            } else {
                actName = act.name
            }
        }

        val actExists = transaction {
            (act != null && Act.all().filterNot { it.id == act.id }
                .any { it.name.trimEnd() == actName.trimEnd() }) || (act == null && Act.all()
                .any { it.name.trimEnd() == actName.trimEnd() })
        }

        if (actExists) {
            return Result.failure(IllegalStateException(StringLocale[ST_ACT_ALREADY_EXISTS]))
        }

        val updatedAct =
            transaction { act?.also { it.name = actName.trimEnd() } ?: Act.new { this.name = actName.trimEnd() } }

        val idList = scenes.mapNotNull { it.id }

        transaction {
            updatedAct.scenes.filter { it.id !in idList }.forEach(Scene::delete)

            scenes.forEach {
                if (it.id != null) with(Scene[it.id]) {
                    this.name = it.name
                    val oldImg = this.background.toImgPath().checkedImgPath()
                    if (it.img.path != background) {
                        this.background = it.img.saveImgAndGetPath()
                        oldImg?.deleteIfExists()
                    }

                } else Scene.new {
                    this.name = it.name
                    this.background = it.img.saveImgAndGetPath()
                    this.idAct = updatedAct.id.value
                }
            }

            if (act == null) {
                updatedAct.currentScene = Scene.find { SceneTable.idAct eq updatedAct.id.value }.first()
            }
        }

        return Result.success
    }

    private fun sceneWithNameExist(name: String, excludedId: EntityID<Int>? = null) =
        scenes.filter { excludedId == null || it.id != excludedId }.any { it.name.trimEnd() == name.trimEnd() }
}