package view.utils.factories

import model.dao.getIcon
import model.dao.localization.*
import view.utils.components.ItemPanel
import viewModel.BlueprintManager
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
        this.add(SquareLabel(Strings[STR_HP], isEditable = false))
        this.add(SquareLabel(Strings[STR_MP], isEditable = false))
        this.add(SquareLabel(Strings[STR_IMG], isEditable = false))
        this.add(SquareLabel(getIcon("create_icon", this.javaClass), manager::createBlueprint))
    }
}

fun buildTitleItemPanel(manager: BlueprintManager): TitlePanel {
    return with(manager.type) {
        if (this == model.element.Type.OBJECT) {
            ObjectTitlePanel(Strings[STR_OBJECTS], manager)
        } else {
            CharacterTitlePanel(this.name, manager)
        }
    }
}