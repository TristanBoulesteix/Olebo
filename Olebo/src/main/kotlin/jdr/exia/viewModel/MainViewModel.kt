package jdr.exia.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.model.act.Act
import jdr.exia.model.element.Element
import jdr.exia.model.type.Point
import jdr.exia.model.utils.callCommandManager
import jdr.exia.view.composable.master.MapPanel
import jdr.exia.view.menubar.MasterMenuBar
import jdr.exia.view.tools.getTokenFromPosition
import jdr.exia.view.tools.positionOf
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Rectangle

class MainViewModel(val act: Act) {
    companion object {
        const val ABSOLUTE_WIDTH = 1600
        const val ABSOLUTE_HEIGHT = 900
    }

    val menuBar by lazy { MasterMenuBar(act = act, viewModel = this) }

    val panel by lazy { MapPanel(isParentMaster = true, viewModel = this) }

    val scene
        get() = transaction { act.currentScene }

    var selectedElements: List<Element> by mutableStateOf(emptyList())
        private set

    var cursor: Point? by mutableStateOf(null)

    /**
     * Returns true if there is at least one element at the given position
     */
    fun hasElementAtPosition(position: Point) = scene.elements.getTokenFromPosition(position) != null

    fun selectElementsAtPosition(position: Point) {
        selectedElements = scene.elements.getTokenFromPosition(position)?.let { listOf(it) } ?: emptyList()
        repaint()
    }

    /**
     * Changes tokens position without dropping them (a moved token stays selected), intended for small steps
     */
    fun moveTokensTo(point: Point, from: Point? = null) {
        val origin = from?.let { pos ->
            selectedElements.find { it.hitBox in pos } ?: selectElementsAtPosition(pos)
                .let { selectedElements.firstOrNull() }
        }

        /**
         * Return a new [Point] inside the bourders of the map
         */
        fun Point.checkBound(): Point {
            var newPosition = this

            if (point.x < 0) {
                newPosition = newPosition.copy(x = 0)
            } else if (newPosition.x > ABSOLUTE_WIDTH) {
                newPosition = newPosition.copy(x = ABSOLUTE_WIDTH)
            }

            if (newPosition.y < 0) {
                newPosition = newPosition.copy(y = 0)
            } else if (newPosition.y > ABSOLUTE_HEIGHT) {
                newPosition = newPosition.copy(y = ABSOLUTE_HEIGHT)
            }

            return newPosition
        }

        val newPosition = point.checkBound()

        if (selectedElements.isNotEmpty()) {
            if (selectedElements.size == 1) {
                selectedElements.first()
                    .cmdPosition(selectedElements.first().positionOf(newPosition), scene.commandManager)
            } else {
                val originElement = (selectedElements.find { it === origin } ?: selectedElements.first())

                val diffPosition = newPosition - originElement.centerPoint

                val elementToPoint =
                    mapOf(originElement to originElement.positionOf(newPosition)) + selectedElements.filterNot { it === originElement }
                        .map { it to it.positionOf((it.centerPoint + diffPosition).checkBound()) }

                scene.callCommandManager(elementToPoint, Element::cmdPosition)
            }
        }

        repaint()
    }

    fun selectElements(rec: Rectangle) {
        val elements = mutableListOf<Element>()

        scene.elements.forEach {
            if (rec.contains(panel.getRelativeRectangleOfToken(it))) {
                elements += it
            }
        }

        selectedElements = elements.ifEmpty { emptyList() }

        repaint()
    }

    fun removeElements(elements: List<Element> = selectedElements) { //removes given token from MutableList
        selectedElements = emptyList()
        scene.callCommandManager(elements, Element::cmdDelete)
        repaint()
    }

    fun unselectElements() {
        selectedElements = emptyList()
        repaint()
    }

    fun repaint() {
        panel.repaint()
        menuBar.reloadCommandItemLabel()
    }
}