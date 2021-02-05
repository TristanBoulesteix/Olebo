package jdr.exia.viewModel

import jdr.exia.localization.ST_SCENE_ALREADY_EXISTS
import jdr.exia.localization.Strings
import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.dao.DAO
import jdr.exia.model.dao.saveImg
import jdr.exia.utils.forElse
import jdr.exia.view.frames.home.dialog.SceneEditorDialog
import jdr.exia.view.utils.showPopup
import jdr.exia.viewModel.observer.Action
import jdr.exia.viewModel.observer.Observable
import jdr.exia.viewModel.observer.Observer
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Manager to create an act (uses all classes in jdr.exia.jdr.exia.view.frames.editor)
 */
class ActCreatorManager : Observable {
    val tempScenes = mutableListOf<SceneData>()

    override var observer: Observer?
        get() = HomeManager.observer
        set(value) {
            HomeManager.observer = value
        }

    private var idAct = 0

    /**
     * Save or create an act into the database.
     */
    fun saveAct(actName: String): Boolean {
        if (DAO.getActsList().map { if (it.first != idAct.toString()) it.second else "" }
                .contains(actName)) return false

        if (idAct == 0) {
            fun createScenes(idCurrentAct: Int): MutableList<Scene> {
                val scenes = mutableListOf<Scene>()

                tempScenes.forEach {
                    val background = saveImg(it.img)

                    scenes += Scene.new {
                        this.name = it.name
                        this.background = background
                        this.idAct = idCurrentAct
                    }
                }

                return scenes
            }

            transaction {
                Act.new {
                    this.name = actName
                }.apply {
                    val scenesList = createScenes(this.id.value)
                    this.scenes += scenesList
                    if (scenesList.isNotEmpty()) this.sceneId = scenesList[0].id.value
                }
            }
        } else {
            transaction {
                with(Act[idAct]) {
                    this.name = actName

                    this.scenes.forEach { scene ->
                        if (!tempScenes.map { it.id ?: -1 }.contains(scene.id.value)) {
                            scene.delete()
                        }
                    }

                    tempScenes.forElse { map ->
                        when {
                            map.id != null -> {
                                val scene = this.scenes.findWithId(map.id)
                                scene?.name = map.name
                                if (scene?.background != map.img) scene?.background = saveImg(map.img)
                            }
                            else -> this.scenes += Scene.new {
                                this.name = map.name
                                this.background = saveImg(map.img)
                                this.idAct = this@with.id.value
                            }
                        }
                    } ?: this.scenes.forEach { it.delete() }
                }
            }
        }

        return true
    }

    /**
     * Show dialog to create a scene and save it as HashMap.
     */
    fun createNewScene(@Suppress("UNUSED_PARAMETER") id: Int) {
        SceneEditorDialog().showDialog()?.let {
            if (tempScenes.map { map -> map.name }.contains(it.name)) {
                showPopup(Strings[ST_SCENE_ALREADY_EXISTS])
                createNewScene(0)
            } else {
                tempScenes += it
            }
        }
        notifyObserver(Action.Refresh)
    }

    /**
     * Show dialog to update a scene and save it as HashMap.
     */
    fun updateNewScene(index: Int) {
        SceneEditorDialog(tempScenes[index]).showDialog()?.let {
            if (tempScenes.map { map ->
                    if (tempScenes[index].name == map.name) "" else map.name
                }.contains(it.name)) {
                showPopup(Strings[ST_SCENE_ALREADY_EXISTS])
                updateNewScene(index)
            } else {
                tempScenes[index] = it
            }
        }
        notifyObserver(Action.Refresh)
    }

    /**
     * Delete a scene in the HashMap list. It does not affect the database.
     */
    fun deleteNewScene(index: Int) {
        tempScenes.removeAt(index)
        notifyObserver(Action.Refresh)
    }

    /**
     * Get act datas stored into the database and save it into variables.
     */
    fun updateAct(scenes: MutableList<Scene>, id: Int): MutableList<SceneData> {
        tempScenes += scenes.map {
            SceneData(it.name, it.background, it.id.value)
        }
        idAct = id
        return tempScenes
    }
}

/**
 * Extension function to convert a MutableList<HashMap<Field, String>> to Array<Pair<String, String>>.
 */
fun MutableList<SceneData>.getArrayOfPairs(): ArrayOfPairs {
    var i = -1

    return this.map {
        i++
        Pair(i.toString(), it.name)
    }.toTypedArray()
}

/**
 * All informations from a scene stored in a class
 */
data class SceneData(val name: String, val img: String, val id: Int? = null)