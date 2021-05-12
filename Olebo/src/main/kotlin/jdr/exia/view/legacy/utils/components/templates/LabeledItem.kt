package jdr.exia.view.legacy.utils.components.templates

import jdr.exia.view.tools.gridBagConstraintsOf
import java.awt.Component
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel

class LabeledItem<T : Component>(label: String, component: T) : JPanel() {
    init {
        this.layout = GridBagLayout()
        this.add(JLabel(label), gridBagConstraintsOf(0, 0))
        this.add(component, gridBagConstraintsOf(1, 0))
    }
}