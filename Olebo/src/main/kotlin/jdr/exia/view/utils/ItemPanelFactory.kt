package jdr.exia.view.utils

import jdr.exia.model.element.Type
import jdr.exia.model.utils.getIcon
import jdr.exia.view.utils.components.ItemPanel
import java.awt.Color
import javax.swing.BorderFactory

abstract class TitlePanel(name: String) : ItemPanel(0, name) {
    init {
        this.border = BorderFactory.createMatteBorder(2, 2, 0, 2, Color.BLACK)
    }
}

class ObjectTitlePanel(name: String) : TitlePanel(name) {
    init {
        this.add(SquareLabel(getIcon("create_icon", this.javaClass)) {})
    }
}

class CharacterTitlePanel(name: String) : TitlePanel(name) {
    init {
        this.add(SquareLabel("PV") {_,_-> println("toto")})
        this.add(SquareLabel("PM"))
        this.add(SquareLabel("Image"))
        this.add(SquareLabel(getIcon("create_icon", this.javaClass)) {})
    }
}

fun buildTitleItemPanel(type: Type = Type.OBJECT): TitlePanel {
    return if (type == Type.OBJECT) {
        ObjectTitlePanel("Objects")
    } else {
        CharacterTitlePanel(type.name)
    }
}