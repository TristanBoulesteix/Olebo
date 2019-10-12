package jdr.exia.controller

import jdr.exia.pattern.observer.Observable
import jdr.exia.pattern.observer.Observer
import jdr.exia.view.editor.SceneCreatorDialog

object ActCreatorManager : Observable {
    override var observer: Observer? = null

    fun createNewScene(id: Int) {
        SceneCreatorDialog().isVisible = true
    }
}