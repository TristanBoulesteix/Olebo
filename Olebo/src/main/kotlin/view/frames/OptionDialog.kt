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

class OptionDialog(parent: JFrame?) : JDialog(parent, "Options", true) {
    private val comboLanguage = JComboBox(arrayOf("fr", "en")).apply {
        this.preferredSize = Dimension(100, 25)
    }

    init {
        this.size = Dimension(500, 250)
        this.setLocationRelativeTo(null)
        this.isResizable = true
        this.layout = GridBagLayout()
        this.defaultCloseOperation = DISPOSE_ON_CLOSE

        JPanel().applyAndAppendTo(
            this,
            gridBagConstraintsOf(
                0,
                0,
                fill = GridBagConstraints.BOTH,
                weightx = 1.0,
                anchor = GridBagConstraints.NORTHWEST,
                weighty = 1.0
            )
        ) {
            this.layout = GridBagLayout()
            this.border = BorderFactory.createTitledBorder("Général")

            this.add(
                LabeledItem("Langue du logiciel :", comboLanguage),
                gridBagConstraintsOf(0, 0, weightx = 1.0, anchor = GridBagConstraints.LINE_START)
            )
            JCheckBox(Strings[STR_AUTO_UPDATE]).applyAndAppendTo(
                this,
                gridBagConstraintsOf(0, 1, weightx = 1.0, anchor = GridBagConstraints.LINE_START)
            ) {
                this.isSelected = Settings.autoUpdate
                this.addItemListener {

                }
            }
        }

        JPanel().applyAndAppendTo(
            this,
            gridBagConstraintsOf(
                0,
                1,
                fill = GridBagConstraints.BOTH,
                weightx = 1.0,
                weighty = 1.0,
                anchor = GridBagConstraints.NORTHWEST
            )
        ) {
            this.layout = GridBagLayout()
            this.border = BorderFactory.createTitledBorder("Apparence")

            this.add(LabeledItem("cursor", JTextField("")), gridBagConstraintsOf(0, 0, weightx = 1.0, anchor = GridBagConstraints.LINE_START))
        }

        JPanel().applyAndAppendTo(this, gridBagConstraintsOf(
            0,
            2,
            fill = GridBagConstraints.BOTH,
            weightx = 1.0,
            weighty = 1.0,
            anchor = GridBagConstraints.SOUTH
        )) {
            this.add(JButton("Enregistrer"))
            this.add(JButton("Annuler"))
            this.add(JButton("Rétablir les paramètres par défauts"))
        }
    }
}