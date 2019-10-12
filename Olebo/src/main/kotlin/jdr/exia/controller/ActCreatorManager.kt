package jdr.exia.controller

import jdr.exia.pattern.observer.Observable
import jdr.exia.pattern.observer.Observer
import jdr.exia.view.editor.SceneCreatorDialog
import jdr.exia.view.editor.SceneCreatorDialog.Field

class ActCreatorManager : Observable {
    private val tempScenes = mutableListOf<HashMap<Field, String>>()

    override var observer: Observer? = null

    fun createNewScene(@Suppress("UNUSED_PARAMETER") id: Int) {
        SceneCreatorDialog().showDialog()?.let {
            tempScenes += it
        }
    }
}