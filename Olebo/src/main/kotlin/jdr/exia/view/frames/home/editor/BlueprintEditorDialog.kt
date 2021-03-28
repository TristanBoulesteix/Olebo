package jdr.exia.view.frames.home.editor

import jdr.exia.localization.*
import jdr.exia.view.utils.applyAndAddTo
import jdr.exia.view.utils.components.filter.IntegerFilter
import jdr.exia.view.utils.components.templates.PlaceholderTextField
import jdr.exia.view.utils.copy
import jdr.exia.view.utils.gridBagConstraintsOf
import jdr.exia.viewModel.BlueprintData
import java.awt.Color
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.text.PlainDocument
import jdr.exia.model.element.Type as TypeElement

/**
 * This JDialog allows us to update or create a blueprint.
 *
 * @param type The type of element to create
 */
class BlueprintEditorDialog(private val type: TypeElement) : JDialog() {
    private val nameField: JTextField

    private val nameErrorLabel: JLabel

    private val lifeField by lazy {
        PlaceholderTextField(0, 11).apply {
            this.preferredSize = Dimension(100, 25)
            (this.document as PlainDocument).documentFilter = IntegerFilter()
        }
    }

    private val manaField by lazy {
        PlaceholderTextField(0, 11).apply {
            this.preferredSize = Dimension(100, 25)
            (this.document as PlainDocument).documentFilter = IntegerFilter()
        }
    }

    private val importImgButton: JButton

    private lateinit var selectedFile: File

    private val importImgErrorLabel: JLabel

    private var canceled = true

    private var fieldsValid = true

    init {
        this.title = StringLocale[STR_NEW_ELEMENT]
        this.modalityType = ModalityType.APPLICATION_MODAL
        this.size = if (type == TypeElement.OBJECT) Dimension(300, 200) else Dimension(300, 300)
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE
        this.isResizable = false
        this.layout = GridBagLayout()
        this.setLocationRelativeTo(null)

        var constraints = gridBagConstraintsOf(0, 0)

        nameField = JTextField().apply {
            this.preferredSize = Dimension(100, 25)
        }

        JPanel().applyAndAddTo(this, constraints) {
            this.preferredSize = Dimension(220, 40)
            this.add(JLabel(StringLocale[STR_NAME_OF_ELEMENT]))
            this.add(nameField)
        }

        nameErrorLabel = JLabel(StringLocale[ST_NAME_OF_BLUEPRINT_REQUIRED]).applyAndAddTo(this, ++constraints) {
            this.foreground = Color.RED
            this.isVisible = false
        }

        if (type != TypeElement.OBJECT) {
            JPanel().applyAndAddTo(this, ++constraints) {
                this.preferredSize = Dimension(220, 60)
                this.add(JLabel(StringLocale[STR_MAX_HEALTH]))
                this.add(lifeField)
            }

            JPanel().applyAndAddTo(this, ++constraints) {
                this.preferredSize = Dimension(220, 60)
                this.add(JLabel(StringLocale[STR_MAX_MANA]))
                this.add(manaField)
            }
        }

        importImgButton = JButton(StringLocale[STR_IMPORT_IMG]).applyAndAddTo(
            this,
            ++constraints
        ) {
            this.addActionListener {
                val file = JFileChooser().apply {
                    this.currentDirectory = File(System.getProperty("user.home"))
                    this.addChoosableFileFilter(
                        FileNameExtensionFilter("Images", *ImageIO.getReaderFileSuffixes())
                    )
                    this.isAcceptAllFileFilterUsed = false
                }
                val result = file.showSaveDialog(this@BlueprintEditorDialog)

                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = file.selectedFile
                    this.toolTipText = selectedFile.name
                }
            }
        }

        importImgErrorLabel = JLabel().applyAndAddTo(this, ++constraints) {
            this.foreground = Color.RED
            this.isVisible = false
        }

        JPanel().applyAndAddTo(
            this,
            ++constraints
        ) {
            JButton(StringLocale[STR_CONFIRM]).applyAndAddTo(this) {
                addActionListener {
                    fieldsValid = if (nameField.text.isNullOrBlank()) {
                        nameField.border = BorderFactory.createLineBorder(Color.RED)
                        nameErrorLabel.isVisible = true
                        false
                    } else {
                        nameField.border = BorderFactory.createLineBorder(Color.BLACK)
                        nameErrorLabel.isVisible = false
                        true
                    }

                    fieldsValid = if (!::selectedFile.isInitialized) {
                        importImgButton.foreground = Color.RED
                        importImgErrorLabel.isVisible = true
                        importImgErrorLabel.text = StringLocale[ST_IMG_BLUEPRINT_REQUIRED]
                        false
                    } else if (!selectedFile.exists()) {
                        importImgButton.foreground = Color.RED
                        importImgErrorLabel.isVisible = true
                        importImgErrorLabel.text = StringLocale[ST_IMG_BLUEPRINT_NOT_EXIST_OR_INVALID]
                        false
                    } else {
                        importImgButton.foreground = Color.BLACK
                        importImgErrorLabel.isVisible = false
                        importImgErrorLabel.text = null
                        true
                    }

                    if (fieldsValid) {
                        canceled = false
                        dispose()
                    }
                }
            }

            JButton(StringLocale[STR_CANCEL]).applyAndAddTo(this) {
                addActionListener { dispose() }
            }

            this.border = BorderFactory.createEmptyBorder(10, 0, 0, 0)
        }
    }


    /**
     * Display the JDialog and return the new blueprint datas.
     */
    fun showDialog(): BlueprintData? {
        this.isVisible = true
        return if (!canceled) BlueprintData(
            nameField.text,
            selectedFile.absolutePath,
            manaField.text.toIntOrNull() ?: 0,
            lifeField.text.toIntOrNull() ?: 0
        ) else null
    }
}

private operator fun GridBagConstraints.inc() = this.copy(gridy = ++this.gridy)