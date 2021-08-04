package jdr.exia.viewModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.awt.ComposePanel
import jdr.exia.localization.STR_CLOSE
import jdr.exia.localization.StringLocale
import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.Type
import jdr.exia.model.tools.callCommandManager
import jdr.exia.model.tools.doIfContainsSingle
import jdr.exia.model.type.Point
import jdr.exia.view.WindowManager
import jdr.exia.view.composable.editor.ElementsView
import jdr.exia.view.composable.master.MapPanel
import jdr.exia.view.tools.DefaultFunction
import jdr.exia.view.tools.applyAndAddTo
import jdr.exia.view.tools.getTokenFromPosition
import jdr.exia.view.tools.positionOf
import jdr.exia.view.ui.DIMENSION_MAIN_WINDOW
import jdr.exia.view.ui.setThemedContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JDialog

class MasterViewModel(val act: Act, val scope: CoroutineScope) {
    companion object {
        const val ABSOLUTE_WIDTH = 1600
        const val ABSOLUTE_HEIGHT = 900
    }

    private val scene
        get() = transaction { act.currentScene }

    val commandManager
        get() = transaction { scene.commandManager }

    val panel = MapPanel(isParentMaster = true, viewModel = this)

    var selectedElements: List<Element> by mutableStateOf(emptyList())
        private set

    var blueprintsGrouped by mutableStateOf(loadBlueprints())
        private set

    /**
     * These are all the [Blueprint] placed on  the current map
     */
    var tokens = transaction { scene.elements }
        private set

    var backGroundImage: BufferedImage = transaction { ImageIO.read(File(scene.background)) }
        private set

    var cursor: Point? by mutableStateOf(null)

    lateinit var reloadMenuBar: DefaultFunction

    /**
     * Returns true if there is at least one element at the given position
     */
    fun hasElementAtPosition(position: Point) = scene.elements.getTokenFromPosition(position) != null

    fun selectElementsAtPosition(position: Point, addToExistingElements: Boolean = false) {
        if (!addToExistingElements) {
            selectedElements = scene.elements.getTokenFromPosition(position)?.let { listOf(it) } ?: emptyList()
        } else {
            scene.elements.getTokenFromPosition(position)?.let { selectedElements = selectedElements + it }
        }
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
         * Return a new [Point] inside the borders of the map
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
                val originElement = selectedElements.find { it === origin } ?: selectedElements.first()

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

    private fun unselectElements() {
        selectedElements = emptyList()
        repaint()
    }

    fun select(up: Boolean = true) = transaction {
        if (selectedElements.isEmpty() && scene.elements.isNotEmpty()) {
            scene.elements.first()
        } else {
            val operation = if (up) fun Int.(list: List<Element>): Int {
                return if (this == list.size - 1) 0 else this + 1
            } else fun Int.(list: List<Element>): Int {
                return if (this == 0) list.size - 1 else this - 1
            }

            selectedElements = selectedElements.doIfContainsSingle(emptyList()) { blueprint ->
                val elements = scene.elements

                if (elements.getOrNull(elements.indexOfFirst { it.id == blueprint.id }
                        .operation(elements)) != null) {
                    listOf(elements[elements.indexOfFirst { it.id == blueprint.id }.operation(elements)])
                } else emptyList()
            }
        }
    }

    fun rotateRight() {
        Element.cmdOrientationToRight(commandManager, selectedElements)
        repaint()
    }

    fun rotateLeft() {
        Element.cmdOrientationToLeft(commandManager, selectedElements)
        repaint()
    }

    fun deleteSelectedElement() {
        val elementsToDelete = selectedElements
        unselectElements()
        Element.cmdDelete(commandManager, elementsToDelete)
        repaint()
    }

    fun addNewElement(blueprint: Blueprint) = scope.launch {
        withContext(Dispatchers.IO) {
            scene.addElement(blueprint)
        }
        repaint()
    }

    fun switchScene(scene: Scene) {
        transaction { act.currentScene = scene }
        selectedElements = emptyList()
        backGroundImage = transaction { ImageIO.read(File(scene.background)) }
        repaint()
    }

    fun showBlueprintEditor() {
        selectedElements = emptyList()

        JDialog(WindowManager.currentFocusedState?.window, true).apply {
            DIMENSION_MAIN_WINDOW.let {
                this.size = it
                this.minimumSize = it
            }

            setLocationRelativeTo(null)

            ComposePanel().applyAndAddTo(this) {
                setThemedContent {
                    ElementsView(onDone = { dispose() }, closeText = StringLocale[STR_CLOSE])
                }
            }
        }.isVisible = true

        blueprintsGrouped = loadBlueprints()
        repaint()
    }

    fun repaint() = scope.launch {
        withContext(Dispatchers.IO) {
            tokens = transaction { scene.elements }
        }

        reloadMenuBar()

        panel.repaint()
    }

    private fun loadBlueprints(): Map<Type, List<Blueprint>> = transaction {
        val items = Blueprint.all().groupBy { it.type }

        (Type.values() + items.keys).associateWith { items[it] ?: emptyList() }
    }
}