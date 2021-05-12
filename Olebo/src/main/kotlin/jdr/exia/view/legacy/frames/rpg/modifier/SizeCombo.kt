package jdr.exia.view.legacy.frames.rpg.modifier

import jdr.exia.localization.STR_SIZE_COMBO_TOOLTIP
import jdr.exia.model.element.Elements
import jdr.exia.model.element.Size
import jdr.exia.view.legacy.utils.components.templates.ComboSelectPanel
import jdr.exia.viewModel.legacy.ViewManager

class SizeCombo(items: Elements? = null) :
    ComboSelectPanel(arrayOf("XS", "S", "M", "L", "XL", "XXL"), items, STR_SIZE_COMBO_TOOLTIP) {
    init {
        addActionListener {
            val newSize = when (selectedItem) {
                "XS" -> Size.XS
                "S" -> Size.S
                "M" -> Size.M
                "L" -> Size.L
                "XL" -> Size.XL
                "XXL" -> Size.XXL
                else -> Size.DEFAULT
            }
            ViewManager.updateSizeToken(newSize)
        }
    }

    override fun setSelectedItem(selected: Any?) {
        this.toSelect = selected.doIfElement("S") {
            it.size.name
        }
    }
}