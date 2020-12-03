package view.frames

import view.utils.applyAndAppendTo
import view.utils.components.LabeledItem
import java.awt.Dimension
import java.awt.GridBagLayout
import javax.swing.*

class OptionDialog(parent: JFrame?) : JDialog(parent, true) {
    private val comboLanguage = JComboBox(arrayOf("fr", "en")).apply {
        this.preferredSize = Dimension(100, 25)
    }

    init {
        this.size = Dimension(550, 270)
        this.setLocationRelativeTo(null)
        this.isResizable = false
        this.defaultCloseOperation = DISPOSE_ON_CLOSE

        JPanel().applyAndAppendTo(this) {
            this.preferredSize = Dimension(220, 60)
            this.layout = GridBagLayout()
            this.border = BorderFactory.createTitledBorder("Général")

            this.add(LabeledItem("lang", comboLanguage))
            this.add(LabeledItem("toto", JTextField("toto")))
        }
    }
}