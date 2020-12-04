package viewModel

import model.act.Act
import model.act.Scene
import model.dao.DAO
import model.dao.saveImg
import model.dao.internationalisation.ST_SCENE_ALREADY_EXISTS
import model.dao.internationalisation.Strings
import org.jetbrains.exposed.sql.transactions.transaction
import utils.forElse
import view.frames.editor.acts.SceneEditorDialog
import view.utils.showPopup
import viewModel.pattern.observer.Action
import viewModel.pattern.observer.Observable
import viewModel.pattern.observer.Observer

/**
 * Manager to create an act (uses all classes in jdr.exia.view.frames.editor)
 */
class ActCreatorManager : Observable {
    val tempScenes = mutableListOf<SceneData>()

    override var observer: Observer? = null

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

            transaction(DAO.database) {
                Act.new {
                    this.name = actName
                }.apply {
                    val scenesList = createScenes(this.id.value)
                    this.scenes += scenesList
                    if (scenesList.isNotEmpty()) this.sceneId = scenesList[0].id.value
                }
            }
        } else {
            transaction(DAO.database) {
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
        notifyObserver(Action.REFRESH)
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
        notifyObserver(Action.REFRESH)
    }

    /**
     * Delete a scene in the HashMap list. It does not affect the database.
     */
    fun deleteNewScene(index: Int) {
        tempScenes.removeAt(index)
        notifyObserver(Action.REFRESH)
    }

    /**
     * Get act datas stored into the database and save it into variables.
     */
    fun updateAct(scenes: MutableList<Scene>, id: Int) {
        tempScenes += scenes.map {
            SceneData(it.name, it.background, it.id.value)
        }.toMutableList()
        idAct = id
        notifyObserver(Action.REFRESH)
    }
}

/**
 * Extension function to convert a MutableList<HashMap<Field, String>> to Array<Pair<String, String>>.
 */
fun MutableList<SceneData>.getArrayOfPairs(): Array<Pair<String, String>> {
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