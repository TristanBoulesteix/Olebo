package jdr.exia.view.frames.rpg.modifier

import jdr.exia.localization.STR_BACKGROUND
import jdr.exia.localization.STR_DEFAULT
import jdr.exia.localization.STR_FOREGROUND
import jdr.exia.localization.Strings
import jdr.exia.model.element.Priority
import jdr.exia.model.utils.Elements
import jdr.exia.view.utils.components.templates.ComboSelectPanel
import jdr.exia.viewModel.ViewManager

class PriorityCombo(items: Elements? = null) :
    ComboSelectPanel(arrayOf(Strings[STR_FOREGROUND], Strings[STR_DEFAULT], Strings[STR_BACKGROUND]), items) {
    init {
        addActionListener {
            val newPriority = when (selectedItem) {
                Strings[STR_BACKGROUND] -> Priority.LOW
                Strings[STR_FOREGROUND] -> Priority.HIGH
                else -> Priority.REGULAR
            }

            ViewManager.updatePriorityToken(newPriority)
        }
    }

    override fun setSelectedItem(selected: Any?) {
        this.toSelect = selected.doIfElement(Strings[STR_DEFAULT]) {
            when (it.priority) {
                Priority.HIGH -> Strings[STR_FOREGROUND]
                Priority.LOW -> Strings[STR_BACKGROUND]
                Priority.REGULAR -> Strings[STR_DEFAULT]
            }
        }
    }
}