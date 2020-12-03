package view.frames

import model.dao.Settings
import model.internationalisation.STR_AUTO_UPDATE
import model.internationalisation.Strings
import view.utils.applyAndAppendTo
import view.utils.components.LabeledItem
import view.utils.gridBagConstraintsOf
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

class OptionDialog(parent: JFrame?) : JDialog(parent, true) {
    private val comboLanguage = JComboBox(arrayOf("fr", "en")).apply {
        this.preferredSize = Dimension(100, 25)
    }

    init {
        this.size = Dimension(550, 270)
        this.setLocationRelativeTo(null)
        this.isResizable = true
        this.layout = GridBagLayout()
        this.defaultCloseOperation = DISPOSE_ON_CLOSE

        JPanel().applyAndAppendTo(
            this,
            gridBagConstraintsOf(0, 0, fill = GridBagConstraints.HORIZONTAL, weightx = 1.0)
        ) {
            this.preferredSize = Dimension(220, 60)
            this.layout = GridBagLayout()
            this.border = BorderFactory.createTitledBorder("Général")

            this.add(LabeledItem("Langue du logiciel", comboLanguage), gridBagConstraintsOf(0, 0, fill = GridBagConstraints.HORIZONTAL, weightx = 1.0))
            JCheckBoxMenuItem(Strings[STR_AUTO_UPDATE]).applyAndAppendTo(this, gridBagConstraintsOf(0, 1, fill = GridBagConstraints.HORIZONTAL, weightx = 1.0)) {
                this.isSelected = Settings.autoUpdate
                this.addItemListener {

                }
            }
        }

        JPanel().applyAndAppendTo(
            this,
            gridBagConstraintsOf(0, 1, fill = GridBagConstraints.HORIZONTAL, weightx = 1.0)
        ) {
            this.preferredSize = Dimension(220, 60)
            this.layout = GridBagLayout()
            this.border = BorderFactory.createTitledBorder("Apparence")

            this.add(LabeledItem("toto", JTextField("toto")))
        }
    }
}