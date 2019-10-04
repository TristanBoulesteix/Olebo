package jdr.exia.view

import java.awt.Color
import java.awt.Graphics
import java.awt.GridBagLayout
import javax.swing.JPanel
import jdr.exia.model.element.Element

// This panel contains the map and all the objects placed within it

class MapPanel : JPanel() {

    var tokens = mutableListOf<Element>();


    fun relativeX(absoluteX: Int): Int{
        return absoluteX*this.width
    }

    fun relativeY(absoluteY: Int): Int {
        return absoluteY*this.height
    }


    init {
        this.layout= GridBagLayout();
        this.background = Color.blue
    }

    override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)

        // graphics.drawImage(,X,Y,null);
    }

    fun refresh() { // refreshes the panel's content
        this.repaint()
    }

    public fun updateTokens(tokens: MutableList<Element>){
        this.tokens = tokens;
    }

    override fun paintComponents(g: Graphics?) {
        for(token in tokens)
            if (g != null) {
                g.drawImage(token.sprite.image,relativeX(token.x),relativeY(token.y),null)
            }
    }


}
