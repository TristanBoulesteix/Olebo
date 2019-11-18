package jdr.exia.view.mainFrame

import jdr.exia.model.element.Element
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JPanel

// This panel contains the map and all the objects placed within it

class MapPanel : JPanel(), MouseListener {

    var backGroundImage: Image? =
        null      //The background... Why are you reading this? Stop!! I said stop!!! You're still doing it, even when you had to scroll sideways... Ok i'm giving up, bye
    var tokens = mutableListOf<Element>() //These are all the tokens placed on  the current map
    var moveAbleElement: Element? = null //Marker that's placed around a token when it is selected
    var selectedElement: Element? = null
    var isMasterMapPanel: Boolean = false

    init {
        this.layout = GridBagLayout();
        this.background = Color.blue
        addMouseListener(this)

    }

    private fun relativeX(absoluteX: Int): Int { //translates an X coordinate in 1600:900px to proportional coords according to this window's size
        return (absoluteX * this.width) / 1600
    }

    private fun relativeY(absoluteY: Int): Int { //translates a y coordinate in 1600:900px to proportional coords according to this window's size
        return (absoluteY * this.height) / 900
    }

    private fun absoluteX(relativeX: Int): Int { // Translates an X coordinate from this window into a 1600:900 X coord
        return (((relativeX.toFloat() / this.width.toFloat())) * 1600).toInt()
    }

    private fun absoluteY(relativeY: Int): Int { // Translates an Y coordinate from this window into a 1600:900 Y coord
        return (((relativeY.toFloat() / this.height.toFloat())) * 900).toInt()
    }

    fun updateTokens(tokens: MutableList<Element>) { //Gets the current token display up to date
        this.tokens = tokens;
    }

    override fun paintComponent(g: Graphics?) {
        if (g != null) {
            g.drawImage(
                backGroundImage,
                0,
                0,
                this.width,
                this.height,
                null
            )

            for (token in tokens) //Display every token one by one
            {
                if ((!isMasterMapPanel) && !(token.isVisible)) {
                } //IF this isn't the GM's map, and if the object is not set to visible, then we don't draw it
                else {
                    if ((isMasterMapPanel) && !(token.isVisible)) {
                        drawInvisibleMarker(token, g)
                    }
                    //rotator.rotate(Math.toRadians(token.orientation * 45.0))
                    g.drawImage(
                        token.sprite.image,
                        relativeX(token.position.x),
                        relativeY(token.position.y),
                        relativeX(token.hitBox.width),
                        relativeY(token.hitBox.height),
                        null
                    )
                }
            }
            if (moveAbleElement != null) { // First, place a marker if there needs to be one, so the token will then be painted over it
                drawMoveableMarker(g)
            }
            if (selectedElement != null) {
                drawSelectedMarker(g)
            }
        }
    }

    private fun drawMoveableMarker(g: Graphics) { //Draws a red rectangle around the currently selected token for movement
        g.color = Color.RED
        g.setPaintMode()
        g.drawRect( //Draws a 1 pixel thick rectangle
            relativeX(moveAbleElement!!.hitBox.x) - 2,
            relativeY(moveAbleElement!!.hitBox.y) - 2,
            relativeX(moveAbleElement!!.hitBox.width) + 2,
            relativeY(moveAbleElement!!.hitBox.height) + 2
        )
    }

    private fun drawSelectedMarker(g: Graphics) { //Draws a red rectangle around the currently selected token for movement
        g.color = Color.GREEN
        g.setPaintMode()
        g.drawRect( //Draws a 1 pixel thick rectangle
            relativeX(selectedElement!!.hitBox.x) - 4,
            relativeY(selectedElement!!.hitBox.y) - 4,
            relativeX(selectedElement!!.hitBox.width) + 8,
            relativeY(selectedElement!!.hitBox.height) + 8
        )
    }

    private fun drawInvisibleMarker(
        token: Element,
        g: Graphics
    ) {//Draws a blue rectangle to signify the GM that a token is invisible to the player
        g.color = Color.BLUE
        g.drawRect( //Draws a 1 pixel thick rectangle
            (relativeX(token.position.x) - 3),
            (relativeY(token.position.y) - 3),
            (relativeX(token.hitBox.width) + 6),
            (relativeY(token.hitBox.height) + 6)
        )
    }

    override fun mouseClicked(p0: MouseEvent?) {  /* /!\ Coordinates are stated in pixels here, not in absolute 1000th /!\ */
        if (isMasterMapPanel) {
            var clickedX = absoluteX(p0!!.x)
            var clickedY = absoluteY(p0.y)

            when (p0.button) //  left button: 1, middle button: 2, Right click: 3
            {
                1 -> ViewFacade.clickNDrop(clickedX, clickedY) //Left click
                2 -> ViewFacade.moveToken(clickedX, clickedY) //Middle button
                3 -> ViewFacade.selectToken(clickedX, clickedY) //Right click
            }
        }
    } //Actions to take when the mouse is clicked

    fun setMoveableElement(token: Element) { //Sets a new selector marker
        moveAbleElement = token
        /*marker = Rectangle(
            relativeX(token.hitBox.x)-2,
            relativeY(token.hitBox.y)-2,
            relativeX(token.hitBox.width)+2,
            relativeY(token.hitBox.height)+2
        )*/
    }


    fun clearMarker() { //Removes the current marker
        moveAbleElement = null
    }

    // Unused mouse methods
    override fun mouseExited(p0: MouseEvent?) {}

    override fun mousePressed(p0: MouseEvent?) {}
    override fun mouseReleased(p0: MouseEvent?) {}
    override fun mouseEntered(p0: MouseEvent?) {}
}
