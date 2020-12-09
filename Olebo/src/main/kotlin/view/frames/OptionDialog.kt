package view.frames

import model.dao.internationalisation.*
import model.dao.option.CursorColor
import model.dao.option.Settings
import model.utils.toJColor
import view.frames.rpg.MasterFrame
import view.frames.rpg.ViewFacade
import view.utils.applyAndAppendTo
import view.utils.components.LabeledItem
import view.utils.gridBagConstraintsOf
import java.awt.*
import javax.swing.*

class OptionDialog(parent: Window) : JDialog(parent as? JFrame, Strings[STR_OPTIONS], true) {
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

    private val checkBoxPlayerFrameOpenedByDefault = JCheckBox(Strings[STR_PLAYERFRAME_OPENED]).apply {
        this.isSelected = Settings.playerFrameOpenedByDefault
    }

    private val comboColorCursor = ComboColorCursor()

    private val checkboxVisibilityElement = JCheckBox(Strings[STR_DEFAULT_ELEMENT_VISIBILITY]).apply {
        this.isSelected = Settings.defaultElementVisibility
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
            this.border = BorderFactory.createTitledBorder(Strings[STR_GENERAL])

            this.add(
                LabeledItem(Strings[STR_SOFTWARE_LANGUAGE_LABEL], comboLanguage),
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
            this.border = BorderFactory.createTitledBorder(Strings[STR_LOOK_AND_FEEL])

            this.add(
                checkBoxPlayerFrameOpenedByDefault,
                gridBagConstraintsOf(0, 0, weightx = 1.0, anchor = GridBagConstraints.LINE_START)
            )

            this.add(
                LabeledItem(Strings[STR_CURSOR_COLOR_LABEL], comboColorCursor),
                gridBagConstraintsOf(0, 1, weightx = 1.0, anchor = GridBagConstraints.LINE_START)
            )

            this.add(
                checkboxVisibilityElement,
                gridBagConstraintsOf(0, 2, weightx = 1.0, anchor = GridBagConstraints.LINE_START)
            )
        }

        languageChangeRestartLabel =
            JLabel(Strings[ST_LANGUAGE_CHANGE_ON_RESTART]).applyAndAppendTo(
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
            JButton(Strings[STR_SAVE]).applyAndAppendTo(this) {
                addActionListener {
                    Settings.language = Strings.availableLocales[comboLanguage.selectedIndex]
                    Settings.autoUpdate = checkBoxAutoUpdate.isSelected
                    Settings.playerFrameOpenedByDefault = checkBoxPlayerFrameOpenedByDefault.isSelected
                    comboColorCursor.selectedCursorColor?.let {
                        Settings.cursorColor = it
                        if (owner is MasterFrame)
                            ViewFacade.updateCursorOnPlayerFrame()
                    }
                    Settings.defaultElementVisibility = checkboxVisibilityElement.isSelected
                    dispose()
                }
            }

            JButton(Strings[STR_CANCEL]).applyAndAppendTo(this) {
                addActionListener {
                    dispose()
                }
            }

            this.add(JButton(Strings[STR_RESTORE_DEFAULTS_OPTIONS]))
        }
    }

    private inner class ComboColorCursor : JComboBox<String>() {
        private val custom by StringDelegate(STR_CUSTOM_COLOR)

        private val customLabel
            get() = when (selectedCursorColor) {
                null -> Settings.cursorColor.let {
                    if (it is CursorColor.Custom) custom + " " + it.contentCursorColor.toString() else custom
                }
                is CursorColor.Custom -> custom + " " + selectedCursorColor!!.contentCursorColor.toString()
                else -> custom
            }

        private val comboColorItems = listOf(
            CursorColor.BLACK_WHITE,
            CursorColor.WHITE_BLACK,
            CursorColor.PURPLE,
            CursorColor.YELLOW,
            CursorColor.RED,
        )

        private var isRefreshing = false

        var selectedCursorColor: CursorColor? = null


        init {
            this.refreshItems(Settings.cursorColor)
            this.addActionListener {
                if (!isRefreshing) {
                    val colorInSettings = Settings.cursorColor

                    val selectedJColor: Color = when {
                        selectedCursorColor == null && colorInSettings is CursorColor.Custom -> colorInSettings.contentCursorColor.toJColor()
                        selectedCursorColor is CursorColor.Custom -> selectedCursorColor!!.contentCursorColor.toJColor()
                        else -> Color.WHITE
                    }

                    selectedCursorColor =
                        comboColorItems.find { it.name == selectedItem }
                            ?: selectColor(selectedJColor)?.let {
                                CursorColor.Custom(it)
                            } ?: selectedCursorColor
                    selectedCursorColor?.let {
                        this.refreshItems(it)
                    }
                }
            }
        }

        private fun selectColor(color: Color): Color? =
            JColorChooser.showDialog(this@OptionDialog, Strings[STR_SELECT_COLOR], color)

        private fun refreshItems(cursorColor: CursorColor) {
            isRefreshing = true
            this.removeAllItems()
            comboColorItems.map { it.name }.forEach(this::addItem)
            this.addItem(customLabel)
            this.selectedItem = selectedItemFromCursorColor(cursorColor.name)?.name ?: customLabel
            isRefreshing = false
        }

        private fun selectedItemFromCursorColor(cursorColorName: String) =
            comboColorItems.find { it.name == cursorColorName }
    }
}