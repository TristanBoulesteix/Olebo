package jdr.exia.view.mainFrame
import jdr.exia.controller.ViewController
import java.awt.Graphics
import java.awt.GridBagLayout
import javax.swing.JPanel
import jdr.exia.model.element.Element
import java.awt.Color
import java.awt.Image
import java.awt.event.MouseEvent
import java.awt.event.MouseListener


// This panel contains the map and all the objects placed within it

class MapPanel : JPanel(), MouseListener {

    init {
        this.layout= GridBagLayout();
        this.background = Color.blue
        addMouseListener(this)
    }

    var backGroundImage: Image? = null
    var tokens = mutableListOf<Element>(); //These are all the tokens placed on  the current map



    private fun relativeX(absoluteX: Int): Int{ //translates an X coordinate in 1600:900px to proportional coords according to this window's size
        return (absoluteX*this.width)/1600
    }
    private fun relativeY(absoluteY: Int): Int { //translates a y coordinate in 1600:900px to proportional coords according to this window's size
        return (absoluteY*this.height)/900
    }

    private fun absoluteX(relativeX: Int): Int { // Translates an X coordinate from this window into a 1600:900 X coord
        return (((relativeX.toFloat()/ this.width.toFloat()))*1600).toInt()
    }
    fun absoluteY(relativeY: Int): Int { // Translates an Y coordinate from this window into a 1600:900 Y coord
        return (((relativeY.toFloat()/ this.height.toFloat()))*900).toInt()
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

                g.drawImage(token.sprite.image,relativeX(token.position.x),relativeY(token.position.y),token.hitBox.width,token.hitBox.height,null)
            }
    }


    override fun mouseClicked(p0: MouseEvent?) {  /* /!\ Coordinates are stated in pixels here, not in absolute 1000th /!\ */

        var clickedX = absoluteX(p0!!.x)
        var clickedY= absoluteY(p0!!.y)

       when(p0.button) //  1, middle button: 2, Right click: 3
       {
           1 -> ViewManager.clickNDrop(clickedX,clickedY) //Left click
           2 -> ViewManager.moveToken(clickedX,clickedY) //Middle button
           3 -> null //Right click
       }
    }




    // Unused mouse methods
    override fun mouseExited(p0: MouseEvent?) {}
    override fun mousePressed(p0: MouseEvent?) {}
    override fun mouseReleased(p0: MouseEvent?) {}
    override fun mouseEntered(p0: MouseEvent?) {}


}
