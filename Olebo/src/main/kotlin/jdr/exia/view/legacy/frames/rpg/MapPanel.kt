package jdr.exia.view.legacy.frames.rpg

import jdr.exia.model.dao.option.SerializableColor
import jdr.exia.model.dao.option.SerializableLabelState
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Element
import jdr.exia.model.element.Elements
import jdr.exia.model.element.Size
import jdr.exia.model.element.emptyElements
import jdr.exia.model.type.Point
import jdr.exia.view.legacy.utils.compareTo
import jdr.exia.view.legacy.utils.drawCircleWithCenterCoordinates
import jdr.exia.view.legacy.utils.event.addMouseExitedListener
import jdr.exia.view.legacy.utils.event.addMouseMovedListener
import jdr.exia.view.legacy.utils.event.addMouseReleasedListener
import jdr.exia.view.legacy.utils.fillCircleWithCenterCoordinates
import jdr.exia.view.legacy.utils.getTokenFromPosition
import jdr.exia.viewModel.legacy.ViewManager
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
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

    private var selectedArea: Rectangle? = null

    var cursorColor: Color

    var borderCursorColor: Color

    var repaintJob: Job? = null
        private set

    init {
        this.layout = GridBagLayout()
        this.background = Color.WHITE
        this.isOpaque = false

        if (parentGameFrame is MasterFrame)
            initializeForMaster()
        else
            initializeForPlayer()

        Settings.cursorColor.let {
            cursorColor = it.contentColor
            borderCursorColor = it.borderColor
        }
    }

    /**
     * init only for [MasterFrame]
     */
    private fun initializeForMaster() {
        var start = Point()
        var movePoint: Point? = null

        this.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(me: MouseEvent) {
                start = me.point
            }

            override fun mouseDragged(me: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    val end = me.point

                    if (ViewManager.positionHasElement(Point(start).absolutePosition)) {
                        movePoint = Point(end)
                        selectedArea = null
                    } else {
                        movePoint = null
                        selectedArea = Rectangle(
                            start.x.coerceAtMost(end.x),
                            start.y.coerceAtMost(end.y),
                            abs(start.x - end.x),
                            abs(start.y - end.y)
                        )

                        this@MapPanel.repaint()
                    }
                }
            }
        })

        addMouseReleasedListener { me ->
            val releasedPosition = Point(me.point).absolutePosition

            when (me.button) {
                MouseEvent.BUTTON1 -> if (movePoint == null && selectedArea == null) ViewManager.selectElement(
                    releasedPosition
                ) // Left click
                MouseEvent.BUTTON2, MouseEvent.BUTTON3 -> ViewManager.moveTokens(releasedPosition)   // Other buttons
            }

            selectedArea?.let {
                if (it.size >= Dimension(Size.XS.size.absoluteSizeValue, Size.XS.size.absoluteSizeValue))
                    ViewManager.selectElements(it)
                else repaint()
                selectedArea = null
            }

            movePoint?.absolutePosition?.let {
                ViewManager.moveTokens(it, Point(start).absolutePosition)
                start = it.toJPoint()
                movePoint = null
            }
        }

        addMouseMovedListener { me ->
            ViewManager.cursorPoint = Point(me.point).absolutePosition
        }

        addMouseExitedListener {
            ViewManager.cursorPoint = null
        }

        ToolTipManager.sharedInstance().registerComponent(this)
    }

    /**
     * init only for [PlayerFrame]
     */
    private fun initializeForPlayer() {
        repaintJob = GlobalScope.launch(Dispatchers.Swing) {
            while (true) {
                if (Settings.cursorEnabled)
                    repaint()
                delay(80L)
            }
        }
    }

    /**
     * Translates an X coordinate in 1600:900px to proportional coords according to this window's size
     */
    private fun relativeX(absoluteX: Int): Int {
        return (absoluteX * this.width) / ViewManager.ABSOLUTE_WIDTH
    }

    /**
     * Translates a y coordinate in 1600:900px to proportional coords according to this window's size
     */
    private fun relativeY(absoluteY: Int): Int {
        return (absoluteY * this.height) / ViewManager.ABSOLUTE_HEIGHT
    }

    /**
     * Translates an X coordinate from this window into a 1600:900 X coord
     */
    private fun absoluteX(relativeX: Int): Int {
        return (((relativeX.toFloat() / this.width.toFloat())) * ViewManager.ABSOLUTE_WIDTH).toInt()
    }

    /**
     * Translates an Y coordinate from this window into a 1600:900 Y coord
     */
    private fun absoluteY(relativeY: Int): Int {
        return (((relativeY.toFloat() / this.height.toFloat())) * ViewManager.ABSOLUTE_HEIGHT).toInt()
    }

    /**
     * Gets the current token display up to date
     */
    fun updateTokens(tokens: Elements) {
        this.tokens = tokens
    }

    val Point.absolutePosition
        get() = Point(absoluteX(x), absoluteY(y))

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        // Draw background image
        (g as Graphics2D).drawImage(
            backGroundImage,
            0,
            0,
            this.width,
            this.height,
            null
        )

        val labelColor = Settings.labelColor
        val labelState = Settings.labelState

        //Display every token one by one
        for (token in tokens) {
            //IF this isn't the GM's map, and if the object is not set to visible, then we don't draw it
            if (parentGameFrame !is PlayerFrame || token.isVisible) {
                // Draw token and visiblity indicator
                if ((parentGameFrame is MasterFrame)) {
                    if (!(token.isVisible)) {
                        g.drawInvisibleMarker(token)
                    }

                    // Draw selection indicator
                    if (selectedElements.isNotEmpty() && token in selectedElements) {
                        g.drawSelectedMarker(token)
                    }
                }
                g.drawToken(token)

                if ((parentGameFrame is MasterFrame && labelState.isVisible) || labelState == SerializableLabelState.FOR_BOTH)
                    g.drawLabel(token, labelColor)
            }
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


    /**
     * Draws a red rectangle around the currently selected token for movement
     */
    private fun Graphics.drawSelectedMarker(token: Element) {
        color = Color.RED
        setPaintMode()
        drawRect( //Draws a 1 pixel thick rectangle
            relativeX(token.referencePoint.x) - 4,
            relativeY(token.referencePoint.y) - 4,
            relativeX(token.hitBox.width) + 8,
            relativeY(token.hitBox.height) + 8
        )
    }

    private fun Graphics.drawLabel(token: Element, labelColor: SerializableColor) {
        val (refX, refY) = token.referencePoint
        val alias = token.alias

        font = Font("Arial", Font.BOLD, 24)
        color = labelColor.contentColor

        val x = relativeX(refX) + (relativeX(token.hitBox.width) - fontMetrics.stringWidth(alias)) / 2
        val y = relativeY(refY) - 10

        drawString(alias, x, y)
    }

    /**
     * Draws a blue rectangle to signify the GM that a token is invisible to the player
     */
    private fun Graphics.drawInvisibleMarker(token: Element) {
        color = Color.BLUE
        drawRect( //Draws a 1 pixel thick rectangle
            (relativeX(token.referencePoint.x) - 3),
            (relativeY(token.referencePoint.y) - 3),
            (relativeX(token.hitBox.width) + 6),
            (relativeY(token.hitBox.height) + 6)
        )
    }

    fun getRelativeRectangleOfToken(token: Element) = Rectangle(
        relativeX(token.referencePoint.x),
        relativeY(token.referencePoint.y),
        relativeX(token.hitBox.width),
        relativeY(token.hitBox.height)
    )

    private fun Graphics.drawToken(token: Element) {
        drawImage(
            token.sprite.image,
            relativeX(token.referencePoint.x),
            relativeY(token.referencePoint.y),
            relativeX(token.hitBox.width),
            relativeY(token.hitBox.height),
            null
        )
    }

    /**
     * Show alias on mouse hover
     */
    override fun getToolTipText() = mousePosition?.let {
        if (Settings.labelState.isEnabled && parentGameFrame is MasterFrame) tokens.getTokenFromPosition(Point(it).absolutePosition)?.alias else null
    }
}
