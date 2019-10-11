package jdr.exia.view.actCreator

import jdr.exia.controller.ActCreatorDialogManager
import jdr.exia.controller.HomeFrameManager
import jdr.exia.model.act.Act
import jdr.exia.model.dao.DAO
import jdr.exia.pattern.observer.Action
import jdr.exia.pattern.observer.Observable
import jdr.exia.view.template.components.JDialogTemplate
import jdr.exia.view.template.components.PlaceholderTextField
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagLayout
import java.awt.Window
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.BorderFactory
import javax.swing.JPanel

class ActCreatorDialog : JDialogTemplate("Nouvel acte", true) {
    override val observable: Observable = HomeFrameManager

    private val selectorPanel = SceneSelectorPanel()
    private val newAct = transaction(DAO.database) {
        Act.new {
            name = "test"
        }
    }

    init {
        ActCreatorDialogManager.observer = this

        this.defaultCloseOperation = DISPOSE_ON_CLOSE
        this.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                DAO.deleteEntity(newAct)
            }
        })
        this.add(JPanel().apply {
            this.border = BorderFactory.createEmptyBorder(15, 10, 15, 10)
            this.layout = GridBagLayout()

            this.add(PlaceholderTextField("Nom"), GridBagConstraints().apply {
                this.weightx = 1.0
                this.fill = BOTH
            })

            this.background = Color.ORANGE
        }, NORTH)

        this.add(selectorPanel, CENTER)
    }

    override fun update(data: Action): Window? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}