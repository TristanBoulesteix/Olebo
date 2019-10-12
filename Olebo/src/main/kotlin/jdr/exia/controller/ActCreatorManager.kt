package jdr.exia.controller

import jdr.exia.pattern.observer.Action
import jdr.exia.pattern.observer.Observable
import jdr.exia.pattern.observer.Observer
import jdr.exia.view.editor.SceneCreatorDialog
import jdr.exia.view.editor.SceneCreatorDialog.Field

class ActCreatorManager : Observable {
    val tempScenes = mutableListOf<HashMap<Field, String>>()

    override var observer: Observer? = null

    fun createNewScene(@Suppress("UNUSED_PARAMETER") id: Int) {
        SceneCreatorDialog().showDialog()?.let {
            tempScenes += it
        }
        notifyObserver(Action.REFRESH)
    }

    fun deleteNewScene(index: Int) {
        println(index)
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