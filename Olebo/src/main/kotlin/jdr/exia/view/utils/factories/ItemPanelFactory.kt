package jdr.exia.view.utils.factories

import jdr.exia.controller.BlueprintManager
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

class ObjectTitlePanel(name: String, manager: BlueprintManager) : TitlePanel(name) {
    init {
        this.add(SquareLabel(getIcon("create_icon", this.javaClass), manager::createBlueprint))
    }
}

class CharacterTitlePanel(name: String, manager: BlueprintManager) : TitlePanel(name) {
    init {
        this.add(SquareLabel("PV", { _, _ -> println("toto") }, isEditable = false))
        this.add(SquareLabel("PM", isEditable = false))
        this.add(SquareLabel("Img", isEditable = false))
        this.add(SquareLabel(getIcon("create_icon", this.javaClass), manager::createBlueprint))
    }
}

fun buildTitleItemPanel(manager: BlueprintManager): TitlePanel {
    return with(manager.type) {
        if (this == Type.OBJECT) {
            ObjectTitlePanel("Objects", manager)
        } else {
            CharacterTitlePanel(this.name, manager)
        }
    }
}