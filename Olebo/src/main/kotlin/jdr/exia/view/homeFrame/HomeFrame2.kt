package jdr.exia.view.homeFrame

import javafx.scene.layout.Priority
import jdr.exia.VERSION
import jdr.exia.controller.HomeManager
import jdr.exia.view.style.CommonStyle
import tornadofx.*

class HomeFrame2 : View("Olebo - Version $VERSION") {
    override val root = vbox {
        addClass(CommonStyle.rootModals)

        anchorpane {
            setId(CommonStyle.buttonPanel)

            button("Éléments") {
                anchorpaneConstraints {
                    bottomAnchor = 10
                    topAnchor = 10
                    leftAnchor = 100
                }

                action {
                    HomeManager.openObjectEditorFrame()
                }
            }

            button("Ajouter un scénario") {
                anchorpaneConstraints {
                    bottomAnchor = 10
                    topAnchor = 10
                    rightAnchor = 100
                }

                action {
                    HomeManager.openActCreatorFrame()
                }
            }
        }

        hbox {
            setId(CommonStyle.actPanel)

            vgrow = Priority.ALWAYS
        }
    }
}
