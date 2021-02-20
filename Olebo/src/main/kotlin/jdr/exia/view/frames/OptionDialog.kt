package jdr.exia.view.frames

import jdr.exia.availableLocales
import jdr.exia.localization.*
import jdr.exia.model.dao.SettingsTable
import jdr.exia.model.dao.option.SerializableColor
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.dao.option.toFormatedString
import jdr.exia.view.frames.rpg.MasterFrame
import jdr.exia.view.frames.rpg.ViewFacade
import jdr.exia.view.utils.applyAndAddTo
import jdr.exia.view.utils.components.templates.LabeledItem
import jdr.exia.view.utils.gridBagConstraintsOf
import jdr.exia.view.utils.showMessage
import jdr.exia.view.utils.windowAncestor
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.*
import javax.swing.*
import kotlin.reflect.KMutableProperty0

class OptionDialog(parent: Window?) : JDialog(parent as? JFrame, Strings[STR_OPTIONS], true) {
    private val comboLanguageItems =
        availableLocales.map { it.getDisplayLanguage(it).capitalize(Settings.language) }.toTypedArray()

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

    private val checkboxLabelEnabled: JCheckBox = JCheckBox(Strings[STR_LABEL_ENABLED]).apply {
        this.isSelected = Settings.isLabelEnabled
        this.addActionListener {
            comboLabelColor.isEnabled = this.isSelected
        }
    }

    private val comboLabelColor: ComboLabelColor = ComboLabelColor().apply {
        this.isEnabled = checkboxLabelEnabled.isSelected
    }

    private val languageChangeRestartLabel: JLabel

    init {
        this.size = Dimension(500, 300)
        this.setLocationRelativeTo(windowAncestor)
        this.isResizable = true
        this.layout = GridBagLayout()
        this.defaultCloseOperation = DISPOSE_ON_CLOSE

        JPanel().applyAndAddTo(
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

        JPanel().applyAndAddTo(
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
                gridBagConstraintsOf(
                    0,
                    1,
                    weightx = 1.0,
                    anchor = GridBagConstraints.LINE_START,
                    insets = Insets(0, 5, 5, 0)
                )
            )

            this.add(
                checkboxVisibilityElement,
                gridBagConstraintsOf(0, 2, weightx = 1.0, anchor = GridBagConstraints.LINE_START)
            )

            this.add(
                checkboxLabelEnabled,
                gridBagConstraintsOf(0, 3, weightx = 1.0, anchor = GridBagConstraints.LINE_START)
            )

            this.add(
                LabeledItem(Strings[STR_LABEL_COLOR], comboLabelColor),
                gridBagConstraintsOf(
                    0,
                    4,
                    weightx = 1.0,
                    anchor = GridBagConstraints.LINE_START,
                    insets = Insets(0, 5, 10, 0)
                )
            )
        }

        languageChangeRestartLabel =
            JLabel(Strings[ST_LANGUAGE_CHANGE_ON_RESTART]).applyAndAddTo(
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

        JPanel().applyAndAddTo(
            this, gridBagConstraintsOf(
                0,
                3,
                fill = GridBagConstraints.BOTH,
                weightx = 1.0,
                weighty = 1.0,
                anchor = GridBagConstraints.SOUTH
            )
        ) {
            JButton(Strings[STR_SAVE]).applyAndAddTo(this) {
                addActionListener {
                    // Save option selected to the database
                    Settings.language = availableLocales[comboLanguage.selectedIndex]
                    Settings.autoUpdate = checkBoxAutoUpdate.isSelected
                    Settings.playerFrameOpenedByDefault = checkBoxPlayerFrameOpenedByDefault.isSelected
                    comboColorCursor.selectedSerializableColor?.let {
                        Settings.cursorColor = it
                        if (owner is MasterFrame)
                            ViewFacade.updateCursorOnPlayerFrame()
                    }
                    Settings.defaultElementVisibility = checkboxVisibilityElement.isSelected
                    checkboxLabelEnabled.isSelected.let { isEnabled ->
                        Settings.isLabelEnabled = isEnabled
                        comboLabelColor.selectedSerializableColor?.let { Settings.labelColor = it }
                        if (owner is MasterFrame)
                            ViewFacade.reloadFrames()
                    }

                    // Close the Options dialog
                    dispose()
                }
            }

            JButton(Strings[STR_CANCEL]).applyAndAddTo(this) {
                addActionListener {
                    dispose()
                }
            }

            JButton(Strings[STR_RESTORE_DEFAULTS_OPTIONS]).applyAndAddTo(this) {
                addActionListener {
                    transaction { SettingsTable.initializeDefault() }
                    showMessage(Strings[ST_DEFAULT_SETTINGS_RESTORED], this)
                    dispose()
                }
            }
        }
    }

    private open inner class ComboColor(
        private val optionPropertyColor: KMutableProperty0<SerializableColor>,
        vararg serializableColors: SerializableColor
    ) :
        JComboBox<String>() {
        private val custom by StringDelegate(STR_CUSTOM_COLOR)

        private val customLabel
            get() = when (selectedSerializableColor) {
                null -> optionPropertyColor.get().let {
                    if (it is SerializableColor.Custom) custom + " " + it.contentColor.toFormatedString() else custom
                }
                is SerializableColor.Custom -> custom + " " + selectedSerializableColor!!.contentColor.toFormatedString()
                else -> custom
            }

        protected open val comboColorItems = listOf(
            *serializableColors,
            SerializableColor.PURPLE,
            SerializableColor.YELLOW,
            SerializableColor.RED,
        )

        private var isRefreshing = false

        var selectedSerializableColor: SerializableColor? = null


        init {
            this.refreshItems(optionPropertyColor.get())
            this.addActionListener {
                if (!isRefreshing) {
                    val colorInSettings = optionPropertyColor.get()

                    val selectedJColor: Color = when {
                        selectedSerializableColor == null && colorInSettings is SerializableColor.Custom -> colorInSettings.contentColor
                        selectedSerializableColor is SerializableColor.Custom -> selectedSerializableColor!!.contentColor
                        else -> Color.WHITE
                    }

                    selectedSerializableColor =
                        comboColorItems.find { it.name == selectedItem }
                            ?: selectColor(selectedJColor)?.let {
                                SerializableColor.Custom(it)
                            } ?: selectedSerializableColor
                    selectedSerializableColor?.let {
                        this.refreshItems(it)
                    }
                }
            }
        }

        private fun selectColor(color: Color): Color? =
            JColorChooser.showDialog(this@OptionDialog, Strings[STR_SELECT_COLOR], color)

        private fun refreshItems(serializableColor: SerializableColor) {
            isRefreshing = true
            this.removeAllItems()
            comboColorItems.map { it.name }.forEach(this::addItem)
            this.addItem(customLabel)
            this.selectedItem = selectedItemFromCursorColor(serializableColor.name)?.name ?: customLabel
            isRefreshing = false
        }

        private fun selectedItemFromCursorColor(cursorColorName: String) =
            comboColorItems.find { it.name == cursorColorName }
    }

    private inner class ComboColorCursor :
        ComboColor(Settings::cursorColor, SerializableColor.BLACK_WHITE, SerializableColor.WHITE_BLACK)

    private inner class ComboLabelColor : ComboColor(Settings::labelColor, SerializableColor.BLACK)
}