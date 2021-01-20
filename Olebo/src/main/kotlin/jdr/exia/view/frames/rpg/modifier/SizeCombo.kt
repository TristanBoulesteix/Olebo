package jdr.exia.view.frames.rpg.modifier

import jdr.exia.model.element.Size
import jdr.exia.model.utils.Elements
import jdr.exia.view.utils.components.templates.ComboSelectPanel
import jdr.exia.viewModel.ViewManager

class SizeCombo(items: Elements? = null) :
    ComboSelectPanel(arrayOf("XS", "S", "M", "L", "XL", "XXL"), items) {
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