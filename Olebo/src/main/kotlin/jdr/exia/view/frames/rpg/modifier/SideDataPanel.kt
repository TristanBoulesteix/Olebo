package jdr.exia.view.frames.rpg.modifier

import jdr.exia.localization.STR_LABEL
import jdr.exia.localization.STR_LABEL_TOOLTIP
import jdr.exia.localization.STR_NAME
import jdr.exia.localization.Strings
import jdr.exia.model.dao.option.Settings
import jdr.exia.view.frames.Reloadable
import jdr.exia.view.utils.DEFAULT_INSET
import jdr.exia.view.utils.components.filter.MaxCharFilter
import jdr.exia.view.utils.components.templates.ComboSelectPanel
import jdr.exia.view.utils.components.templates.PlaceholderTextField
import jdr.exia.view.utils.components.templates.ValidableField
import jdr.exia.view.utils.event.addFocusGainedListener
import jdr.exia.view.utils.gridBagConstraintsOf
import jdr.exia.viewModel.ViewManager
import java.awt.Dimension
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.text.AbstractDocument
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SideDataPanel : JPanel(), Reloadable {
    val blueprintNameLabel = object : JLabel() {
        init {
            this.horizontalTextPosition = LEFT
        }

        override fun setText(text: String?) {
            if (text == null) {
                this.isEnabled = false
                super.setText(Strings[STR_NAME])
            } else {
                this.isEnabled = true
                super.setText(text)
            }
        }
    }

    private val nameLabel = PlaceholderTextField(Strings[STR_LABEL]).apply {
        this.font = Font(this.font.name, Font.PLAIN, 15)
        this.preferredSize = Dimension(110, this.preferredSize.height)
        this.toolTipText = Strings[STR_LABEL_TOOLTIP]
        this.addFocusGainedListener {
            this.selectAll()
        }
        (this.document as AbstractDocument).documentFilter = MaxCharFilter(14)
    }

    val nameLabelPanel = ValidableField(nameLabel) { _, text ->
        ViewManager.updateLabel(text)
    }.apply {
        this.isEnabled = false
    }

    var sizeCombo by reloadLayoutAfterSet(SizeCombo())

    var priorityCombo by reloadLayoutAfterSet(PriorityCombo())

    init {
        this.layout = GridBagLayout()
        this.isOpaque = false
        this.reload()
    }

    private fun initLayout() {
        this.removeAll()
        if (Settings.isLabelEnabled)
            loadLabelLayout()
        else
            loadUnlabelLayout()
    }

    private fun loadLabelLayout() {
        this.add(
            blueprintNameLabel, gridBagConstraintsOf(
                0,
                0,
                weightx = 1.0,
                insets = DEFAULT_INSET,
                anchor = GridBagConstraints.LINE_START,
                fill = GridBagConstraints.HORIZONTAL
            )
        )

        this.add(
            nameLabelPanel, gridBagConstraintsOf(
                0,
                1,
                weightx = 1.0,
                insets = DEFAULT_INSET,
                anchor = GridBagConstraints.LINE_START,
                fill = GridBagConstraints.HORIZONTAL
            )
        )

        this.add(
            sizeCombo, gridBagConstraintsOf(
                1,
                0,
                weightx = 1.0,
                insets = DEFAULT_INSET,
                anchor = GridBagConstraints.LINE_START,
                fill = GridBagConstraints.HORIZONTAL
            )
        )

        this.add(
            priorityCombo, gridBagConstraintsOf(
                1,
                1,
                weightx = 1.0,
                insets = DEFAULT_INSET,
                anchor = GridBagConstraints.LINE_START,
                fill = GridBagConstraints.HORIZONTAL
            )
        )
    }

    private fun loadUnlabelLayout() {
        this.add(
            blueprintNameLabel, gridBagConstraintsOf(
                0,
                0,
                weightx = 1.0,
                insets = DEFAULT_INSET,
                anchor = GridBagConstraints.LINE_START,
                fill = GridBagConstraints.HORIZONTAL
            )
        )

        this.add(
            sizeCombo, gridBagConstraintsOf(
                0,
                1,
                weightx = 1.0,
                insets = DEFAULT_INSET,
                anchor = GridBagConstraints.LINE_START,
                fill = GridBagConstraints.HORIZONTAL
            )
        )

        this.add(
            priorityCombo, gridBagConstraintsOf(
                0,
                2,
                weightx = 1.0,
                insets = DEFAULT_INSET,
                anchor = GridBagConstraints.LINE_START,
                fill = GridBagConstraints.HORIZONTAL
            )
        )
    }

    override fun reload() = initLayout()

    private fun <T : ComboSelectPanel> reloadLayoutAfterSet(combo: T) = object : ReadWriteProperty<SideDataPanel, T> {
        private var combo = combo

        override operator fun setValue(thisRef: SideDataPanel, property: KProperty<*>, value: T) {
            this.combo = value
            initLayout()
        }

        override operator fun getValue(thisRef: SideDataPanel, property: KProperty<*>): T = this.combo
    }
}