package jdr.exia.view.editor.elements

import jdr.exia.view.utils.IntegerFilter
import jdr.exia.view.utils.showPopup
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
 * @param blueprint The blueprint to update. If the scene is <strong>null</strong>, it will be created.
 */
class BlueprintEditorDialog(private val type: TypeElement, private val blueprint: HashMap<Field, String>? = null) :
    JDialog() {
    private val nameField = JTextField(if (blueprint != null) blueprint[Field.NAME] else null).apply {
        this.preferredSize = Dimension(100, 25)
    }
    private val lifeField by lazy {
        JTextField(if (blueprint != null) blueprint[Field.LIFE] else null).apply {
            this.preferredSize = Dimension(100, 25)
            (this.document as PlainDocument).documentFilter = IntegerFilter()
        }
    }
    private val manaField by lazy {
        JTextField(if (blueprint != null) blueprint[Field.MANA] else null).apply {
            this.preferredSize = Dimension(100, 25)
            (this.document as PlainDocument).documentFilter = IntegerFilter()
        }
    }

    /**
     * Check if all field are valid
     */
    private val isFieldValid: Boolean
        get() {
            val validManaAndLife =
                if (type == TypeElement.OBJECT) true else (lifeField.text.isNotBlank()) && (manaField.text.isNotBlank())
            return (nameField.text.isNotBlank()) && validManaAndLife && (::selectedFile.isInitialized) && (selectedFile.exists())
        }

    private lateinit var selectedFile: File
    private var canceled = true

    init {
        fun <T : JButton> T.addAction(action: () -> Unit): T {
            this.addActionListener { action() }
            return this
        }

        this.title = if (blueprint == null) "Nouvel élément" else "Modification de l'élément"
        this.modalityType = ModalityType.APPLICATION_MODAL
        this.size = Dimension(400, 300)
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE
        this.isResizable = false
        this.layout = GridBagLayout()
        this.setLocationRelativeTo(null)

        this.add(JPanel().apply {
            this.preferredSize = Dimension(220, 60)
            this.add(JLabel("Nom de l'élément :"))
            this.add(nameField)
        }, GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = 0
        })

        if (type != TypeElement.OBJECT) {
            this.add(JPanel().apply {
                this.preferredSize = Dimension(220, 60)
                this.add(JLabel("PV max :"))
                this.add(lifeField)
            }, GridBagConstraints().apply {
                this.gridx = 0
                this.gridy = 1
            })

            this.add(JPanel().apply {
                this.preferredSize = Dimension(220, 60)
                this.add(JLabel("PM max :"))
                this.add(manaField)
            }, GridBagConstraints().apply {
                this.gridx = 0
                this.gridy = 2
            })
        }

        this.add(JButton("Importer une image").apply {
            this.toolTipText = if (blueprint != null) {
                selectedFile = File(blueprint[Field.IMG]!!)
                selectedFile.name
            } else null
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
        }, GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = if (type == TypeElement.OBJECT) 1 else 3
        })

        this.add(JPanel().apply {
            this.add(JButton("Valider").addAction { this@BlueprintEditorDialog.isVisible = false; canceled = false })
            this.add(JButton("Annuler").addAction { this@BlueprintEditorDialog.isVisible = false })
            this.border = BorderFactory.createEmptyBorder(10, 0, 0, 0)
        }, GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = if (type == TypeElement.OBJECT) 2 else 4
        })
    }

    /**
     * Display the JDialog and return the new blueprint datas.
     */
    fun showDialog(): HashMap<Field, String>? {
        this.isVisible = true
        return if (isFieldValid && !canceled) {
            hashMapOf(
                Field.NAME to nameField.text,
                Field.IMG to selectedFile.absolutePath
            ).also {
                if (type != TypeElement.OBJECT) {
                    it += hashMapOf(
                        Field.MANA to manaField.text,
                        Field.LIFE to lifeField.text
                    )
                }
                if (blueprint?.get(Field.ID) != null)
                    it[Field.ID] = blueprint[Field.ID]
            }
        } else if (!canceled) {
            this.canceled = true
            showPopup("Le nom existe déjà ou le fichier sélectionné est invalide !", this)
            return this.showDialog()
        } else null
    }

    /**
     * All blueprints datas type
     */
    enum class Field {
        NAME, IMG, ID, LIFE, MANA
    }
}