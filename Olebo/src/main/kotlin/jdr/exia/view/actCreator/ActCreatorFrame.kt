package jdr.exia.view.actCreator

import jdr.exia.controller.HomeFrameController
import jdr.exia.pattern.Action
import jdr.exia.pattern.Observable
import jdr.exia.view.template.components.JFrameTemplate

class ActCreatorFrame : JFrameTemplate("Nouvel acte") {
    override val observable: Observable = HomeFrameController

    init {
        this.defaultCloseOperation = DISPOSE_ON_CLOSE
    }

    override fun update(data: Action) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}