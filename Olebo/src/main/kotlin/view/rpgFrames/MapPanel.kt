package view.rpgFrames

import model.element.Element
import viewModel.ViewManager
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionAdapter
import javax.swing.JPanel
import javax.swing.SwingUtilities


// This panel contains the map and all the objects placed within it

class MapPanel(private val isMasterMapPanel: Boolean = false) : JPanel(), MouseListener {
    var backGroundImage: Image? = null      //The background... Why are you reading this? Stop!! I said stop!!! You're still doing it, even when you had to scroll sideways... Ok i'm giving up, bye
    var tokens = mutableListOf<Element>() //These are all the tokens placed on  the current map
    var selectedElements = mutableListOf<Element>()

    var selectedArea: Rectangle? = null

    init {
        this.layout = GridBagLayout()
        this.background = Color.blue
        if (isMasterMapPanel)
            this.addMouseMotionListener(object : MouseMotionAdapter() {
                private var start = Point()

                override fun mouseMoved(me: MouseEvent) {
                        start = me.point
                        this@MapPanel.repaint()
                }

                override fun mouseDragged(me: MouseEvent) {
                    if (SwingUtilities.isLeftMouseButton(me)) {
                        val end = me.point
                        selectedArea = Rectangle(start,
                                Dimension(end.x - start.x, end.y - start.y))
                        this@MapPanel.repaint()
                    }
                }
            })
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
        this.tokens = tokens
    }

    override fun paintComponent(g: Graphics) {
        (g as Graphics2D).drawImage(
                backGroundImage,
                0,
                0,
                this.width,
                this.height,
                null
        )

        for (token in tokens) //Display every token one by one
        {
            if ((!isMasterMapPanel) && !(token.isVisible)) { //Do NOTHING
            } //IF this isn't the GM's map, and if the object is not set to visible, then we don't draw it
            else {
                if ((isMasterMapPanel) && !(token.isVisible)) {
                    drawInvisibleMarker(token, g)
                }
                drawTokenUp(token, g)
            }
        }
        if (selectedElements.isNotEmpty() && this.isMasterMapPanel) {
            drawSelectedMarker(g)
        }

        if (selectedArea != null) {
            g.color = Color.RED
            g.draw(selectedArea)
            g.color = Color(255, 255, 255, 150)
            g.fill(selectedArea)
        }
    }


    private fun drawSelectedMarker(g: Graphics) { //Draws a red rectangle around the currently selected token for movement
        g.color = Color.RED
        g.setPaintMode()
        selectedElements.forEach {
            g.drawRect( //Draws a 1 pixel thick rectangle
                    relativeX(it.position.x) - 4,
                    relativeY(it.position.y) - 4,
                    relativeX(it.hitBox.width) + 8,
                    relativeY(it.hitBox.height) + 8
            )
        }
    }

    private fun drawInvisibleMarker(token: Element, g: Graphics) {//Draws a blue rectangle to signify the GM that a token is invisible to the player
        g.color = Color.BLUE
        g.drawRect( //Draws a 1 pixel thick rectangle
                (relativeX(token.position.x) - 3),
                (relativeY(token.position.y) - 3),
                (relativeX(token.hitBox.width) + 6),
                (relativeY(token.hitBox.height) + 6)
        )
    }

    override fun mousePressed(p0: MouseEvent) {  /* Reacts to the user's click and calls the corresponding function */
        if (isMasterMapPanel) {
            selectedArea = null

            val clickedX = absoluteX(p0.x)
            val clickedY = absoluteY(p0.y)

            when (p0.button) //  left button: 1, middle button: 2, Right click: 3
            {
                1 -> ViewManager.selectElement(clickedX, clickedY) //Left click
                2 -> ViewFacade.moveToken(clickedX, clickedY)   //Middle button
                3 -> ViewFacade.moveToken(clickedX, clickedY)   //Right click
            }
        }
    } //Actions to take when the mouse is clicked

    override fun mouseReleased(p0: MouseEvent) {
        selectedArea?.let {
            ViewManager.selectElements(it)
        }
        selectedArea = null
    }

    fun drawToken(token: Element, g: Graphics) { //draws a token with the adequate orientation TODO: implement rotation
        when (token.orientation) {
            0 -> drawTokenUp(token, g)
        }

    }

    fun getRelativeRectangleOfToken(token: Element) = Rectangle(
            relativeX(token.position.x),
            relativeY(token.position.y),
            relativeX(token.hitBox.width),
            relativeY(token.hitBox.height)
    )

    private fun drawTokenUp(token: Element, g: Graphics) {
        g.drawImage(token.sprite.image,
                relativeX(token.position.x),
                relativeY(token.position.y),
                relativeX(token.hitBox.width),
                relativeY(token.hitBox.height),
                null)
    }

    private fun drawTokenRight(token: Element, g: Graphics) {
        val g2d = g as Graphics2D
        g2d.rotate(Math.toRadians(90.0))
        val printX = token.y
        val printY = 900 - token.x
        g.drawImage(token.sprite.image,
                0,
                0,
                relativeX(token.hitBox.width),
                relativeY(token.hitBox.height),
                null)
        g2d.rotate(Math.toRadians(270.0))

    }


    // Unused mouse methods
    override fun mouseExited(p0: MouseEvent) {}
    override fun mouseClicked(p0: MouseEvent) {}
    override fun mouseEntered(p0: MouseEvent) {}
}