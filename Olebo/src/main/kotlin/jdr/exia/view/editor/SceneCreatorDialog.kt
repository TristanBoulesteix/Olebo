package jdr.exia.view.editor

import java.awt.Color
import java.awt.Dimension
import javax.swing.*



class SceneCreatorDialog : JDialog() {
    init {
        this.title = "Nouvelle scène"
        this.modalityType = ModalityType.APPLICATION_MODAL
        this.size = Dimension(400, 300)
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE
        this.isResizable = false
        this.setLocationRelativeTo(null)

        val panNom = JPanel()
        panNom.background = Color.white
        panNom.preferredSize = Dimension(220, 60)
        val nom = JTextField()
        nom.preferredSize = Dimension(100, 25)
        panNom.border = BorderFactory.createTitledBorder("Nom du personnage")
        val nomLabel = JLabel("Saisir un nom :")
        panNom.add(nomLabel)
        panNom.add(nom)
        this.add(panNom)
    }
}