package jdr.exia.view.frames.rpg.modifier

import jdr.exia.localization.*
import jdr.exia.model.element.Elements
import jdr.exia.model.element.Priority
import jdr.exia.view.utils.components.templates.ComboSelectPanel
import jdr.exia.viewModel.ViewManager

class PriorityCombo(items: Elements? = null) :
    ComboSelectPanel(
        arrayOf(StringLocale[STR_FOREGROUND], StringLocale[STR_DEFAULT], StringLocale[STR_BACKGROUND]),
        items,
        STR_PRIORITY_COMBO_TOOLTIP
    ) {
    init {
        addActionListener {
            val newPriority = when (selectedItem) {
                StringLocale[STR_BACKGROUND] -> Priority.LOW
                StringLocale[STR_FOREGROUND] -> Priority.HIGH
                else -> Priority.REGULAR
            }

            ViewManager.updatePriorityToken(newPriority)
        }
    }

    override fun setSelectedItem(selected: Any?) {
        this.toSelect = selected.doIfElement(StringLocale[STR_DEFAULT]) {
            when (it.priority) {
                Priority.HIGH -> StringLocale[STR_FOREGROUND]
                Priority.LOW -> StringLocale[STR_BACKGROUND]
                Priority.REGULAR -> StringLocale[STR_DEFAULT]
            }
        }
    }
}