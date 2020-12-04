package view.frames

import model.dao.Settings
import model.dao.internationalisation.STR_AUTO_UPDATE
import model.dao.internationalisation.Strings
import view.utils.applyAndAppendTo
import view.utils.components.LabeledItem
import view.utils.gridBagConstraintsOf
import java.awt.*
import javax.swing.*

class OptionDialog(parent: Window) : JDialog(parent as? JFrame, "Options", true) {
    private val comboLanguageItems =
        Strings.availableLocales.map { it.getDisplayLanguage(it).capitalize(Settings.language) }.toTypedArray()

    private val comboLanguage =
        JComboBox<String>().apply {
            comboLanguageItems.forEach(this::addItem)
            val itemSelectedBase = comboLanguageItems.find {
                it.equals(
                    Settings.language.getDisplayLanguage(Settings.language),
                    ignoreCase = true
                )
            } ?: comboLanguageItems[0]
            this.selectedItem = itemSelectedBase
            this.preferredSize = Dimension(100, 25)
            this.addActionListener {
                languageChangeRestartLabel.isVisible = selectedItem != itemSelectedBase
            }
        }

    private val languageChangeRestartLabel: JLabel

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

            this.add(
                LabeledItem("cursor", JTextField("")),
                gridBagConstraintsOf(0, 0, weightx = 1.0, anchor = GridBagConstraints.LINE_START)
            )
        }

        languageChangeRestartLabel =
            JLabel("Le changement de langue sera effectif au prochain redémarrage de Olebo.").applyAndAppendTo(
                this, gridBagConstraintsOf(
                    0,
                    2,
                    fill = GridBagConstraints.BOTH,
                    weightx = 1.0,
                    weighty = 1.0,
                    anchor = GridBagConstraints.NORTHWEST
                )
            ) {
                this.foreground = Color.RED
                this.isVisible = false
            }

        JPanel().applyAndAppendTo(
            this, gridBagConstraintsOf(
                0,
                3,
                fill = GridBagConstraints.BOTH,
                weightx = 1.0,
                weighty = 1.0,
                anchor = GridBagConstraints.SOUTH
            )
        ) {
            JButton("Enregistrer").applyAndAppendTo(this) {
                addActionListener {
                    Settings.language = Strings.availableLocales[comboLanguage.selectedIndex]
                    dispose()
                }
            }

            JButton("Annuler").applyAndAppendTo(this) {
                addActionListener {
                    dispose()
                }
            }

            this.add(JButton("Rétablir les paramètres par défauts"))
        }
    }
}