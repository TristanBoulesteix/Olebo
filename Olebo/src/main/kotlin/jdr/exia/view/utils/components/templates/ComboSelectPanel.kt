package jdr.exia.view.utils.components.templates

import jdr.exia.localization.Strings
import jdr.exia.model.element.Element
import jdr.exia.model.utils.Elements
import javax.swing.JComboBox
import javax.swing.border.EmptyBorder

abstract class ComboSelectPanel(items: Array<String>, elements: Elements?, tooltipKey: String) :
    JComboBox<String>(items) {
    init {
        selectedItem = elements
        border = EmptyBorder(0, 0, 0, 0)
        toolTipText = Strings[tooltipKey]
    }

    abstract override fun setSelectedItem(selected: Any?)

    protected var toSelect: Any?
        get() = selectedItem
        set(value) = super.setSelectedItem(value)

    protected inline fun Any?.doIfElement(default: String, actionReturn: (Element) -> String) =
        if (this is String) {
            isEnabled = false
            this
        } else if (this == null || this !is List<*> || this.isEmpty() || this[0] !is Element) {
            isEnabled = false
            default
        } else {
            isEnabled = true
            actionReturn(this[0] as Element)
        }
}