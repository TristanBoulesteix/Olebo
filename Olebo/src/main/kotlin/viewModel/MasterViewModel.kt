package jdr.exia.viewModel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.Type
import jdr.exia.model.tools.callCommandManager
import jdr.exia.model.tools.doIfContainsSingle
import jdr.exia.model.tools.withSetter
import jdr.exia.model.type.Point
import jdr.exia.view.composable.master.MapPanel
import jdr.exia.view.tools.getTokenFromPosition
import jdr.exia.view.tools.positionOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class MasterViewModel(val act: Act, val scope: CoroutineScope) {
    companion object {
        const val ABSOLUTE_WIDTH = 1600
        const val ABSOLUTE_HEIGHT = 900
    }

    var blueprintEditorDialogVisible by mutableStateOf(false)
        private set

    var confirmClearElement by mutableStateOf(false)

    var currentScene by mutableStateOf(transaction { act.currentScene }) withSetter {
        transaction { act.currentScene = it }
    }

    val commandManager by derivedStateOf { currentScene.commandManager }

    val panel = MapPanel(isParentMaster = true, viewModel = this)

    var selectedElements: List<Element> by mutableStateOf(emptyList())
        private set

    var blueprintsGrouped by mutableStateOf(loadBlueprints())
        private set

    /**
     * These are all the [Blueprint] placed on  the current map
     */
    var tokens = transaction { currentScene.elements }
        private set

    val backGroundImage: BufferedImage by derivedStateOf { transaction { ImageIO.read(File(currentScene.background)) } }

    var cursor: Point? by mutableStateOf(null)

    /**
     * Returns true if there is at least one element at the given position
     */
    fun hasElementAtPosition(position: Point) = tokens.getTokenFromPosition(position) != null

    fun selectElementsAtPosition(position: Point, addToExistingElements: Boolean = false) {
        if (!addToExistingElements) {
            selectedElements = tokens.getTokenFromPosition(position)?.let { listOf(it) } ?: emptyList()
        } else {
            tokens.getTokenFromPosition(position)?.let { selectedElements = selectedElements + it }
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
        fun Point.checkBoundOf(element: Element?): Point {
            var newPosition = this

            val height: Int
            val width: Int

            element?.hitBox.let {
                height = (it?.height ?: 0) / 2
                width = (it?.width ?: 0) / 2
            }

            if (newPosition.x < 0) {
                newPosition = newPosition.copy(x = 0 + width)
            } else if (newPosition.x > ABSOLUTE_WIDTH) {
                newPosition = newPosition.copy(x = ABSOLUTE_WIDTH - width)
            }

            if (newPosition.y < 0) {
                newPosition = newPosition.copy(y = 0 + height)
            } else if (newPosition.y > ABSOLUTE_HEIGHT) {
                newPosition = newPosition.copy(y = ABSOLUTE_HEIGHT - height)
            }

            return newPosition
        }

        val newPosition = point.checkBoundOf(origin)

        if (selectedElements.isNotEmpty()) {
            if (selectedElements.size == 1) {
                selectedElements.first()
                    .cmdPosition(selectedElements.first().positionOf(newPosition), currentScene.commandManager)
            } else {
                val originElement = selectedElements.find { it === origin } ?: selectedElements.first()

                val diffPosition = newPosition - originElement.centerPoint

                val elementToPoint =
                    mapOf(originElement to originElement.positionOf(newPosition)) + selectedElements.filterNot { it === originElement }
                        .map { it to it.positionOf((it.centerPoint + diffPosition).checkBoundOf(it)) }

                currentScene.callCommandManager(elementToPoint, Element::cmdPosition)
            }
        }

        repaint()
    }

    fun selectAllElements() {
        selectedElements = tokens

        repaint()
    }

    fun selectElements(rec: Rectangle) {
        val elements = mutableListOf<Element>()

        tokens.forEach {
            if (rec.contains(panel.getRelativeRectangleOfToken(it))) {
                elements += it
            }
        }

        selectedElements = elements

        repaint()
    }

    private fun unselectElements() {
        selectedElements = emptyList()
        repaint()
    }

    fun select(up: Boolean = true) = transaction {
        if (selectedElements.isEmpty() && tokens.isNotEmpty()) {
            tokens.first()
        } else {
            val operation = if (up) fun Int.(list: List<Element>): Int {
                return if (this == list.size - 1) 0 else this + 1
            } else fun Int.(list: List<Element>): Int {
                return if (this == 0) list.size - 1 else this - 1
            }

            selectedElements = selectedElements.doIfContainsSingle(emptyList()) { blueprint ->
                val elements = tokens

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

    /**
     * Remove all elements given as parameter. If no parameter is provided, remove all selected elements
     */
    fun removeElements(elements: List<Element> = selectedElements) { //removes given token from MutableList
        unselectElements()
        currentScene.callCommandManager(elements, Element::cmdDelete)

        tokens = tokens.toMutableList().also {
            it -= elements
        }

        repaint()
    }

    fun addNewElement(blueprint: Blueprint) {
        val newElement = currentScene.addElement(blueprint)

        tokens = tokens.toMutableList().also {
            it += newElement
        }

        repaint()
    }

    fun switchScene(scene: Scene) {
        this.currentScene = scene
        selectedElements = emptyList()
        repaint(reloadTokens = true)
    }

    fun showBlueprintEditor() {
        selectedElements = emptyList()

        blueprintEditorDialogVisible = true
    }

    fun hideBlueprintEditor() {
        blueprintEditorDialogVisible = false

        blueprintsGrouped = loadBlueprints()

        repaint(reloadTokens = true)
    }

    fun moveElementsFromScene(elements: List<Element>) {
        Scene.moveElementToScene(currentScene, elements)

        tokens = tokens.toMutableList().also {
            it += elements
        }

        repaint()
    }

    fun repaint(reloadTokens: Boolean = false) = scope.launch {
        if (reloadTokens)
            withContext(Dispatchers.IO) {
                tokens = transaction { currentScene.elements }
            }

        panel.repaint()
    }

    private fun loadBlueprints(): Map<Type, List<Blueprint>> = transaction {
        val items = Blueprint.all().groupBy { it.type }

        (Type.values() + items.keys).associateWith { items[it] ?: emptyList() }
    }
}