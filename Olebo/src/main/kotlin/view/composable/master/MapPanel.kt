package jdr.exia.view.composable.master

import jdr.exia.model.dao.option.SerializableColor
import jdr.exia.model.dao.option.SerializableLabelState
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Element
import jdr.exia.model.element.Size
import jdr.exia.model.type.Point
import jdr.exia.view.tools.compareTo
import jdr.exia.view.tools.drawCircleWithCenterCoordinates
import jdr.exia.view.tools.event.addMouseExitedListener
import jdr.exia.view.tools.event.addMouseMovedListener
import jdr.exia.view.tools.event.addMouseReleasedListener
import jdr.exia.view.tools.fillCircleWithCenterCoordinates
import jdr.exia.view.tools.getTokenFromPosition
import jdr.exia.viewModel.MasterViewModel
import jdr.exia.viewModel.MasterViewModel.Companion.ABSOLUTE_HEIGHT
import jdr.exia.viewModel.MasterViewModel.Companion.ABSOLUTE_WIDTH
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.ToolTipManager
import kotlin.math.abs

class MapPanel(private val isParentMaster: Boolean, private val viewModel: MasterViewModel) : JComponent() {
    private var selectedArea: Rectangle? = null

    init {
        this.background = Color.WHITE
        this.isOpaque = false

        if (isParentMaster)
            initializeForMaster()
    }

    /**
     * init only for DM window
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

                    if (viewModel.hasElementAtPosition(Point(start).absolutePosition)) {
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
                MouseEvent.BUTTON1 -> if (movePoint == null && selectedArea == null) viewModel.selectElementsAtPosition(
                    releasedPosition,
                    me.isControlDown
                ) // Left click
                MouseEvent.BUTTON2, MouseEvent.BUTTON3 -> viewModel.moveTokensTo(releasedPosition)   // Other buttons
            }

            selectedArea?.let {
                if (it.size >= Dimension(Size.XS.size.absoluteSizeValue, Size.XS.size.absoluteSizeValue))
                    viewModel.selectElements(it)
                else repaint()
                selectedArea = null
            }

            movePoint?.absolutePosition?.let {
                viewModel.moveTokensTo(it, Point(start).absolutePosition)
                start = it.toJPoint()
                movePoint = null
            }
        }

        addMouseMovedListener { me ->
            viewModel.cursor = Point(me.point).absolutePosition
        }

        addMouseExitedListener {
            viewModel.cursor = null
        }

        ToolTipManager.sharedInstance().registerComponent(this)
    }

    /**
     * Translates an X coordinate in 1600:900px to proportional coords according to this window's size
     */
    private fun relativeX(absoluteX: Int): Int {
        return (absoluteX * this.width) / ABSOLUTE_WIDTH
    }

    /**
     * Translates a y coordinate in 1600:900px to proportional coords according to this window's size
     */
    private fun relativeY(absoluteY: Int): Int {
        return (absoluteY * this.height) / ABSOLUTE_HEIGHT
    }

    /**
     * Translates an X coordinate from this window into a 1600:900 X coordinate
     */
    private fun absoluteX(relativeX: Int): Int {
        return (((relativeX.toFloat() / this.width.toFloat())) * ABSOLUTE_WIDTH).toInt()
    }

    /**
     * Translates an Y coordinate from this window into a 1600:900 Y coordinate
     */
    private fun absoluteY(relativeY: Int): Int {
        return (((relativeY.toFloat() / this.height.toFloat())) * ABSOLUTE_HEIGHT).toInt()
    }

    val Point.absolutePosition
        get() = Point(absoluteX(x), absoluteY(y))

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        // Draw background image
        (g as Graphics2D).drawImage(
            viewModel.backGroundImage,
            0,
            0,
            this.width,
            this.height,
            null
        )

        val labelColor = Settings.labelColor
        val labelState = Settings.labelState

        //Display every token one by one
        for (token in viewModel.tokens) {
            //IF this isn't the GM's map, and if the object is not set to visible, then we don't draw it
            if (isParentMaster || token.isVisible) {
                // Draw token and visibility indicator
                if (isParentMaster) {
                    if (!token.isVisible) {
                        g.drawInvisibleMarker(token)
                    }

                    // Draw selection indicator
                    if (viewModel.selectedElements.isNotEmpty() && token in viewModel.selectedElements) {
                        g.drawSelectedMarker(token)
                    }
                }

                g.drawToken(token)

                if ((isParentMaster && labelState.isVisible) || labelState == SerializableLabelState.FOR_BOTH)
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
        if (!isParentMaster && Settings.cursorEnabled)
            viewModel.cursor?.let {
                val (cursorColor, borderCursorColor) = Settings.cursorColor

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
            token.sprite,
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
    override fun getToolTipText() = mousePosition?.let { point ->
        if (isParentMaster) viewModel.tokens.getTokenFromPosition(Point(point).absolutePosition)?.alias.takeIf { !it.isNullOrBlank() } else null
    }
}