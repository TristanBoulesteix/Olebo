package jdr.exia.view.editor

import jdr.exia.view.utils.showPopup
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

class SceneEditorDialog(private val scene: HashMap<Field, String>? = null) : JDialog() {
    private val nameField = JTextField(if (scene != null) scene[Field.NAME] else null).apply {
        this.preferredSize = Dimension(100, 25)
    }

    private lateinit var selectedFile: File
    private var canceled = true

    init {
        fun <T : JButton> T.addAction(action: () -> Unit): T {
            this.addActionListener { action() }
            return this
        }

        this.title = if (scene != null) "Nouvelle scène" else "Modification de la scène"
        this.modalityType = ModalityType.APPLICATION_MODAL
        this.size = Dimension(200, 200)
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE
        this.isResizable = false
        this.layout = GridBagLayout()
        this.setLocationRelativeTo(null)

        this.add(JPanel().apply {
            this.preferredSize = Dimension(220, 60)
            this.add(JLabel("Nom de la scène :"))
            this.add(nameField)
        }, GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = 0
        })

        this.add(JButton("Importer une image").apply {
            this.toolTipText = if (scene != null) {
                selectedFile = File(scene[Field.IMG]!!)
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
                val result = file.showSaveDialog(this@SceneEditorDialog)

                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = file.selectedFile
                    this.toolTipText = selectedFile.name
                }
            }
        }, GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = 1
        })

        this.add(JPanel().apply {
            this.add(JButton("Valider").addAction { this@SceneEditorDialog.isVisible = false; canceled = false })
            this.add(JButton("Annuler").addAction { this@SceneEditorDialog.isVisible = false })
            this.border = BorderFactory.createEmptyBorder(10, 0, 0, 0)
        }, GridBagConstraints().apply {
            this.gridx = 0
            this.gridy = 2
        })
    }

    fun showDialog(): HashMap<Field, String>? {
        this.isVisible = true
        return if (validateField() && !canceled) {
            hashMapOf(
                Field.NAME to nameField.text,
                Field.IMG to selectedFile.absolutePath
            ).also {
                if (scene != null && scene[Field.ID] != null)
                    it[Field.ID] = scene[Field.ID]
            }
        } else if (!canceled) {
            this.canceled = true
            showPopup("Le nom existe déjà ou le fichier sélectionné est invalide !", this)
            return this.showDialog()
        } else null
    }

    private fun validateField(): Boolean {
        return (nameField.text != "") && (::selectedFile.isInitialized) && (selectedFile.exists())
    }

    enum class Field {
        NAME, IMG, ID
    }
}