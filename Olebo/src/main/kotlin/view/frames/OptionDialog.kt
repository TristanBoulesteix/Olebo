package view.frames

import model.dao.internationalisation.STR_AUTO_UPDATE
import model.dao.internationalisation.Strings
import model.dao.option.CursorColor
import model.dao.option.Settings
import view.frames.rpg.MasterFrame
import view.frames.rpg.ViewFacade
import view.utils.applyAndAppendTo
import view.utils.components.LabeledItem
import view.utils.gridBagConstraintsOf
import java.awt.*
import java.awt.event.ItemEvent
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

    private val checkBoxAutoUpdate = JCheckBox(Strings[STR_AUTO_UPDATE]).apply {
        this.isSelected = Settings.autoUpdate
    }

    private val comboColorCursor = ComboColorCursor()

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
            this.add(
                checkBoxAutoUpdate,
                gridBagConstraintsOf(0, 1, weightx = 1.0, anchor = GridBagConstraints.LINE_START)
            )
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
                LabeledItem("Couleur du curseur", comboColorCursor),
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
                    Settings.autoUpdate = checkBoxAutoUpdate.isSelected
                    comboColorCursor.selectedCursorColor?.let {
                        Settings.cursorColor = it
                        if(owner is MasterFrame)
                            ViewFacade.updateCursorOnPlayerFrame()
                    }
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

    private inner class ComboColorCursor : JComboBox<String>() {
        private val custom = "Custom"

        private val customLabel
            get() = when (selectedCursorColor) {
                null -> Settings.cursorColor.let {
                    if (it is CursorColor.CUSTOM) custom + " " + it.contentCursorColor.toString() else custom
                }
                is CursorColor.CUSTOM -> custom + " " + selectedCursorColor!!.contentCursorColor.toString()
                else -> custom
            }

        private val comboColorItems = listOf(
            CursorColor.BLACK_WHITE,
            CursorColor.WHITE_BLACK,
            CursorColor.PURPLE
        )

        private var isRefreshing = false

        var selectedCursorColor: CursorColor? = null


        init {
            this.refreshItems(Settings.cursorColor)
            this.addItemListener { actionEvent ->
                if (!isRefreshing && actionEvent.stateChange == ItemEvent.SELECTED) {
                    selectedCursorColor =
                        comboColorItems.find { it.name == actionEvent.item } ?: selectColor()?.let {
                            CursorColor.CUSTOM(it)
                        } ?: selectedCursorColor
                    selectedCursorColor?.let {
                        this.refreshItems(it)
                    }

                }
            }
        }

        private fun selectColor(): Color? = JColorChooser.showDialog(this@OptionDialog, "Title", Color.WHITE)

        private fun refreshItems(cursorColor: CursorColor) {
            isRefreshing = true
            this.removeAllItems()
            comboColorItems.map { it.name }.forEach(this::addItem)
            this.addItem(customLabel)
            this.selectedItem = comboColorItems.find { it.name == cursorColor.name }?.name ?: customLabel
            isRefreshing = false
        }
    }
}