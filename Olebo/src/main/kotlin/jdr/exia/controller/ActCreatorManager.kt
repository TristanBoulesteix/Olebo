package jdr.exia.controller

import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.dao.DAO
import jdr.exia.model.utils.saveImg
import jdr.exia.pattern.observer.Action
import jdr.exia.pattern.observer.Observable
import jdr.exia.pattern.observer.Observer
import jdr.exia.view.editor.SceneEditorDialog
import jdr.exia.view.editor.SceneEditorDialog.Field
import jdr.exia.view.utils.showPopup
import org.jetbrains.exposed.sql.transactions.transaction

class ActCreatorManager : Observable {
    val tempScenes = mutableListOf<HashMap<Field, String>>()

    override var observer: Observer? = null

    fun createAct(actName: String): Boolean {
        if (DAO.getActsList().map { it.second }.contains(actName)) return false

        fun createScenes(idCurrentAct: Int): MutableList<Scene> {
            val scenes = mutableListOf<Scene>()

            tempScenes.forEach {
                val background = saveImg(it[Field.IMG]!!)

                scenes += Scene.new {
                    this.name = it[Field.NAME]!!
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

        return true
    }

    fun createNewScene(@Suppress("UNUSED_PARAMETER") id: Int) {
        SceneEditorDialog().showDialog()?.let {
            if (tempScenes.map { map -> map[Field.NAME] }.contains(it[Field.NAME])) {
                showPopup("Une scène avec le même nom existe déjà !")
                createNewScene(0)
            } else {
                tempScenes += it
            }
        }
        notifyObserver(Action.REFRESH)
    }

    fun updateNewScene(index: Int) {
        SceneEditorDialog(tempScenes[index]).showDialog()?.let {
            if (tempScenes.map { map ->
                    if (tempScenes[index][Field.NAME] == map[Field.NAME]) "" else map[Field.NAME]
                }.contains(it[Field.NAME])) {
                showPopup("Une scène avec le même nom existe déjà !")
                updateNewScene(index)
            } else {
                tempScenes[index] = it
            }
        }
        notifyObserver(Action.REFRESH)
    }

    fun deleteNewScene(index: Int) {
        tempScenes.removeAt(index)
        notifyObserver(Action.REFRESH)
    }
}

fun MutableList<HashMap<Field, String>>.getArrayOfPairs(): Array<Pair<String, String>> {
    var i = -1

    return this.map {
        i++
        Pair(i.toString(), it[Field.NAME]!!)
    }.toTypedArray()
}