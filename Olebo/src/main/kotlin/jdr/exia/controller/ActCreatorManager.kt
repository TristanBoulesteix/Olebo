package jdr.exia.controller

import jdr.exia.pattern.observer.Action
import jdr.exia.pattern.observer.Observable
import jdr.exia.pattern.observer.Observer
import jdr.exia.view.editor.SceneEditorDialog
import jdr.exia.view.editor.SceneEditorDialog.Field
import jdr.exia.view.utils.showPopup

class ActCreatorManager : Observable {
    val tempScenes = mutableListOf<HashMap<Field, String>>()

    override var observer: Observer? = null

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