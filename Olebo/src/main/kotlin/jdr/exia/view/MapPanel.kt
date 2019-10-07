package jdr.exia.view
import java.awt.Graphics
import java.awt.GridBagLayout
import javax.swing.JPanel
import jdr.exia.model.element.Element
import java.awt.Color
import java.awt.Image


// This panel contains the map and all the objects placed within it

class MapPanel : JPanel() {

    var backGroundImage: Image? = null
    var tokens = mutableListOf<Element>(); //These are all the tokens placed on  the current map


    fun relativeX(absoluteX: Int): Int{ //translates an X coordinate in 1000th to a relative coordinate on this panel
        return absoluteX*this.width
    }

    fun relativeY(absoluteY: Int): Int { //translates a y coordinate in 1000th to a relative coordinate on this panel
        return absoluteY*this.height
    }


    init {
        this.layout= GridBagLayout();
        this.background = Color.blue

    }



    fun refresh() { // refreshes the panel's content
        this.repaint()
    }

    public fun updateTokens(tokens: MutableList<Element>){
        this.tokens = tokens;
    }

    override fun paintComponent(g: Graphics?) {
        g?.drawImage(backGroundImage,0,0,this.width,this.height,null)
        for(token in tokens)
            if (g != null) {
                g.drawImage(token.sprite.image,relativeX(token.x),relativeY(token.y),null)
            }
    }


}
