package jdr.exia.view.editor.elements

import jdr.exia.controller.BlueprintData
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
class BlueprintEditorDialog(private val type: TypeElement, private val blueprint: BlueprintData? = null) :
        JDialog() {
    private val nameField = JTextField(blueprint?.name).apply {
        this.preferredSize = Dimension(100, 25)
    }

    private val lifeField by lazy {
        JTextField(blueprint?.life?.toString()).apply {
            this.preferredSize = Dimension(100, 25)
            (this.document as PlainDocument).documentFilter = IntegerFilter()
        }
    }

    private val manaField by lazy {
        JTextField(blueprint?.mana?.toString()).apply {
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
                selectedFile = File(blueprint.img)
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
    fun showDialog(): BlueprintData? {
        this.isVisible = true
        return if (isFieldValid && !canceled) {
            BlueprintData(
                    nameField.text,
                    selectedFile.absolutePath,
                    if (type != TypeElement.OBJECT) manaField.text.toInt() else null,
                    if (type != TypeElement.OBJECT) lifeField.text.toInt() else null,
                    blueprint?.id
            )
        } else if (!canceled) {
            this.canceled = true
            showPopup("Le nom existe déjà ou le fichier sélectionné est invalide !", this)
            return this.showDialog()
        } else null
    }


}