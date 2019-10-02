package jdr.exia.view

import java.awt.Image
import javax.swing.JFrame

/*PlayerFrame is the Frame the Players can see, it shares its content with MasterFrame
this is a singleton*/
object PlayerFrame : JFrame() {
    private val mapPanel = MapPanel()

    init {
        this.title = "Player"
        this.isResizable = false
        this.setSize(1920, 1080)
        this.contentPane = mapPanel

        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE




    }

    fun placeElementOnMap(tokens: MutableList<ElementPlaceHolder>){
            this.mapPanel.updateTokens(tokens)

    }

}
