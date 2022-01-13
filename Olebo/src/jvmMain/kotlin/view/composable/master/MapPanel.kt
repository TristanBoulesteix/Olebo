package jdr.exia.view.composable.master

import androidx.compose.ui.geometry.Offset
import jdr.exia.model.dao.option.SerializableColor
import jdr.exia.model.dao.option.SerializableLabelState
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Element
import jdr.exia.model.element.SizeElement
import jdr.exia.model.type.Offset
import jdr.exia.model.type.toJColor
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
        var start = Offset(0f, 0f)
        var movePoint: Offset? = null

        this.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(me: MouseEvent) {
                start = Offset(me.point)
            }

            override fun mouseDragged(me: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    val end = Offset(me.point)

                    if (viewModel.hasElementAtPosition(start.absolutePosition)) {
                        movePoint = end
                        selectedArea = null
                    } else {
                        movePoint = null
                        selectedArea = Rectangle(
                            start.x.coerceAtMost(end.x).toInt(),
                            start.y.coerceAtMost(end.y).toInt(),
                            abs(start.x - end.x).toInt(),
                            abs(start.y - end.y).toInt()
                        )

                        repaintLocked = true

                        this@MapPanel.repaint()
                    }
                }
            }
        })

        addMouseReleasedListener { me ->
            repaintLocked = false

            val releasedPosition = Offset(me.point).absolutePosition

            when (me.button) {
                MouseEvent.BUTTON1 -> if (movePoint == null && selectedArea == null) viewModel.selectElementsAtPosition(
                    releasedPosition,
                    me.isControlDown
                ) // Left click
                MouseEvent.BUTTON2, MouseEvent.BUTTON3 -> viewModel.moveTokensTo(releasedPosition)   // Other buttons
            }

            selectedArea?.let {
                if (it.size >= Dimension(SizeElement.XS.value, SizeElement.XS.value))
                    viewModel.selectElements(it)
                else repaint()
                selectedArea = null
            }

            movePoint?.absolutePosition?.let {
                viewModel.moveTokensTo(it, start.absolutePosition)
                start = it
                movePoint = null
            }
        }

        addMouseMovedListener { me ->
            viewModel.cursor = if (me.isAltDown) null else Offset(me.point).absolutePosition
        }

        addMouseExitedListener {
            viewModel.cursor = null
        }

        ToolTipManager.sharedInstance().registerComponent(this)
    }

    /**
     * Translates an X coordinate in 1600:900px to proportional coords according to this window's size
     */
    private fun relativeX(absoluteX: Float): Float {
        return (absoluteX * this.width) / ABSOLUTE_WIDTH
    }

    /**
     * Translates a y coordinate in 1600:900px to proportional coords according to this window's size
     */
    private fun relativeY(absoluteY: Float): Float {
        return (absoluteY * this.height) / ABSOLUTE_HEIGHT
    }

    /**
     * Translates an X coordinate from this window into a 1600:900 X coordinate
     */
    private fun absoluteX(relativeX: Float): Float {
        return (relativeX / this.width.toFloat()) * ABSOLUTE_WIDTH
    }

    /**
     * Translates an Y coordinate from this window into a 1600:900 Y coordinate
     */
    private fun absoluteY(relativeY: Float): Float {
        return (relativeY / this.height.toFloat()) * ABSOLUTE_HEIGHT
    }

    val Offset.absolutePosition
        get() = Offset(absoluteX(x), absoluteY(y))

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        // Draw background image
        (g as Graphics2D).drawImage(
            viewModel.backgroundImage,
            0,
            0,
            this.width,
            this.height,
            null
        )

        val labelColor = Settings.labelColor
        val labelState = Settings.labelState

        //Display every token one by one
        for (token in viewModel.elements) {
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

                g.color = cursorColor.toJColor()
                g.fillCircleWithCenterCoordinates(relativeX(it.x), relativeY(it.y), 15)
                g.color = borderCursorColor.toJColor()
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
            (relativeX(token.referenceOffset.x) - 4).toInt(),
            (relativeY(token.referenceOffset.y) - 4).toInt(),
            (relativeX(token.hitBox.width.toFloat()) + 8).toInt(),
            (relativeY(token.hitBox.height.toFloat()) + 8).toInt()
        )
    }

    private fun Graphics.drawLabel(token: Element, labelColor: SerializableColor) {
        val (refX, refY) = token.referenceOffset
        val alias = token.alias

        font = Font("Arial", Font.BOLD, 24)
        color = labelColor.contentColor.toJColor()

        val x = relativeX(refX) + (relativeX(token.hitBox.width.toFloat()) - fontMetrics.stringWidth(alias)) / 2
        val y = relativeY(refY) - 10

        drawString(alias, x.toInt(), y.toInt())
    }

    /**
     * Draws a blue rectangle to signify the GM that a token is invisible to the player
     */
    private fun Graphics.drawInvisibleMarker(token: Element) {
        color = Color.BLUE
        drawRect( //Draws a 1 pixel thick rectangle
            (relativeX(token.referenceOffset.x) - 3).toInt(),
            (relativeY(token.referenceOffset.y) - 3).toInt(),
            (relativeX(token.hitBox.width.toFloat()) + 6).toInt(),
            (relativeY(token.hitBox.height.toFloat()) + 6).toInt()
        )
    }

    fun getRelativeRectangleOfToken(token: Element) = Rectangle(
        relativeX(token.referenceOffset.x).toInt(),
        relativeY(token.referenceOffset.y).toInt(),
        relativeX(token.hitBox.width.toFloat()).toInt(),
        relativeY(token.hitBox.height.toFloat()).toInt()
    )

    private fun Graphics.drawToken(token: Element) {
        drawImage(
            token.sprite,
            relativeX(token.referenceOffset.x).toInt(),
            relativeY(token.referenceOffset.y).toInt(),
            relativeX(token.hitBox.width.toFloat()).toInt(),
            relativeY(token.hitBox.height.toFloat()).toInt(),
            null
        )
    }

    /**
     * Show alias on mouse hover
     */
    override fun getToolTipText() = mousePosition?.let { point ->
        if (isParentMaster) viewModel.elements.getTokenFromPosition(Offset(point).absolutePosition)?.alias.takeIf { !it.isNullOrBlank() } else null
    }

    /**
     * Call [JComponent.repaint] only if [repaintLocked] is set to [false].
     */
    override fun repaint() {
        if (isParentMaster || !repaintLocked)
            super.repaint()
    }

    private companion object {
        /**
         * If set to [true], the [MapPanel] of the PlayerDialog will not be repainted.
         */
        var repaintLocked = false
    }
}