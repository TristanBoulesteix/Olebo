package jdr.exia.view.frames.rpg

import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Element
import jdr.exia.model.element.Size
import jdr.exia.model.utils.Elements
import jdr.exia.model.utils.emptyElements
import jdr.exia.model.utils.toJColor
import jdr.exia.view.utils.*
import jdr.exia.view.utils.event.addMousePressedListener
import jdr.exia.view.utils.event.addMouseReleasedListener
import jdr.exia.viewModel.ViewManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.ToolTipManager
import kotlin.math.abs

/**
 * This panel contains the map and all the objects placed within it
 */
class MapPanel(private val parentGameFrame: GameFrame) : JPanel() {
    //The background... Why are you reading this? Stop!! I said stop!!! You're still doing it, even when you had to scroll sideways... Ok i'm giving up, bye
    var backGroundImage: Image? = null
    private var tokens = emptyElements() //These are all the tokens placed on  the current map
    var selectedElements = emptyElements()

    var selectedArea: Rectangle? = null

    var cursorColor: Color

    var borderCursorColor: Color

    init {
        this.layout = GridBagLayout()
        this.background = Color.WHITE
        this.isOpaque = false

        if (parentGameFrame is MasterFrame)
            initializeForMaster()
        else
            initializeForPlayer()

        Settings.cursorColor.let {
            cursorColor = it.contentCursorColor.toJColor()
            borderCursorColor = it.borderCursorColor.toJColor()
        }
    }

    /**
     * init only for [MasterFrame]
     */
    private fun initializeForMaster() {
        this.addMouseMotionListener(object : MouseMotionAdapter() {
            private var start = Point()

            override fun mouseMoved(me: MouseEvent) {
                start = me.point
            }

            override fun mouseDragged(me: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    val end = me.point

                    selectedArea = Rectangle(
                        start.x.coerceAtMost(end.x),
                        start.y.coerceAtMost(end.y),
                        abs(start.x - end.x),
                        abs(start.y - end.y)
                    )

                    this@MapPanel.repaint()
                }
            }
        })

        addMousePressedListener {
            selectedArea = null

            val clickedX = absoluteX(it.x)
            val clickedY = absoluteY(it.y)

            when (it.button) //  left button: 1, middle button: 2, Right click: 3
            {
                MouseEvent.BUTTON1 -> ViewManager.selectElement(clickedX, clickedY) //Left click
                MouseEvent.BUTTON2 -> ViewFacade.moveToken(clickedX, clickedY)   //Middle button
                MouseEvent.BUTTON3 -> ViewFacade.moveToken(clickedX, clickedY)   //Right click
            }
        }

        addMouseReleasedListener {
            selectedArea?.let {
                if (it.size >= Dimension(Size.XS.size.absoluteSizeValue, Size.XS.size.absoluteSizeValue))
                    ViewManager.selectElements(it)
                else repaint()
            }
            selectedArea = null
        }

        ToolTipManager.sharedInstance().registerComponent(this)
    }

    /**
     * init only for [PlayerFrame]
     */
    private fun initializeForPlayer() {
        GlobalScope.launch {
            while (true) {
                if (Settings.cursorEnabled)
                    repaint()
                delay(70L)
            }
        }
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

    fun updateTokens(tokens: Elements) { //Gets the current token display up to date
        this.tokens = tokens
    }

    fun getAbsolutePoint(point: Point) = Point(absoluteX(point.x), absoluteY(point.y))

    override fun paintComponent(g: Graphics) {
        // Draw background image
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
            if ((parentGameFrame is PlayerFrame) && !(token.isVisible)) { //Do NOTHING
            } //IF this isn't the GM's map, and if the object is not set to visible, then we don't draw it
            else {
                // Draw token and visiblity indicator
                if ((parentGameFrame is MasterFrame) && !(token.isVisible)) {
                    drawInvisibleMarker(token, g)
                }
                drawToken(token, g)
            }
        }
        // Draw selection indicator
        if (selectedElements.isNotEmpty() && parentGameFrame is MasterFrame) {
            drawSelectedMarker(g)
        }

        // Draw select area
        if (selectedArea != null) {
            g.color = Color.RED
            g.draw(selectedArea)
            g.color = Color(255, 255, 255, 150)
            g.fill(selectedArea)
        }

        // Draw cursor
        if (parentGameFrame is PlayerFrame && Settings.cursorEnabled)
            ViewManager.cursorPoint?.let {
                g.color = cursorColor
                g.fillCircleWithCenterCoordinates(relativeX(it.x), relativeY(it.y), 15)
                g.color = borderCursorColor
                g.drawCircleWithCenterCoordinates(relativeX(it.x), relativeY(it.y), 15)
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

    fun getRelativeRectangleOfToken(token: Element) = Rectangle(
        relativeX(token.position.x),
        relativeY(token.position.y),
        relativeX(token.hitBox.width),
        relativeY(token.hitBox.height)
    )

    private fun drawToken(token: Element, g: Graphics) {
        g.drawImage(
            token.sprite.image,
            relativeX(token.position.x),
            relativeY(token.position.y),
            relativeX(token.hitBox.width),
            relativeY(token.hitBox.height),
            null
        )
    }

    /**
     * Show alias on mouse hover
     */
    override fun getToolTipText() = (mousePosition).let {
        tokens.getTokenFromPoint(getAbsolutePoint(it))?.alias
    }
}
