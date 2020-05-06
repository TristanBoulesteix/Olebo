package jdr.exia.view.style

import javafx.scene.paint.Color
import tornadofx.*

class CommonStyle : Stylesheet() {
    companion object {
        val rootModals by cssclass()

        val buttonPanel by cssid()
        val actPanel by cssid()
    }

    init {
        button {
            padding = box(15.0.px)
            borderRadius = multi(box(0.px))
            backgroundRadius = multi(box(0.px))
        }

        buttonPanel {
            backgroundColor += c(255, 200, 0)
        }

        actPanel {
            backgroundColor += Color.BLUE
        }
    }
}