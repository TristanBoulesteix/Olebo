package jdr.exia.viewModel

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import fr.olebo.sharescene.*
import fr.olebo.sharescene.connection.*
import io.ktor.websocket.*
import jdr.exia.OLEBO_VERSION_CODE
import jdr.exia.localization.STR_DELETE_SELECTED_TOKENS
import jdr.exia.localization.StringLocale
import jdr.exia.localization.get
import jdr.exia.model.act.Act
import jdr.exia.model.act.Scene
import jdr.exia.model.command.Command
import jdr.exia.model.dao.option.Preferences
import jdr.exia.model.dao.option.SerializableLabelState
import jdr.exia.model.dao.option.Settings
import jdr.exia.model.element.Blueprint
import jdr.exia.model.element.Element
import jdr.exia.model.element.Layer
import jdr.exia.model.element.TypeElement
import jdr.exia.model.tools.callCommandManager
import jdr.exia.model.tools.doIfContainsSingle
import jdr.exia.model.tools.settableMutableStateOf
import jdr.exia.model.type.contains
import jdr.exia.model.type.inputStreamFromString
import jdr.exia.service.socketClient
import jdr.exia.view.tools.contains
import jdr.exia.view.tools.getTokenFromPosition
import jdr.exia.view.tools.positionOf
import jdr.exia.viewModel.tags.BlueprintFilter
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import javax.imageio.ImageIO

@Stable
class MasterViewModel(val act: Act, private val scope: CoroutineScope) {
    var blueprintEditorDialogVisible by mutableStateOf(false)
        private set

    var confirmClearElement by mutableStateOf(false)

    var currentScene by settableMutableStateOf(transaction { act.currentScene }) {
        transaction { act.currentScene = it }
    }

    val commandManager by derivedStateOf { currentScene.commandManager }

    var selectedElements: List<Element> by mutableStateOf(emptyList())
        private set

    val hasSelectedElement by derivedStateOf { selectedElements.isNotEmpty() }

    private var blueprintsGrouped by mutableStateOf(loadBlueprints())

    @Stable
    var searchString by mutableStateOf("")

    @Stable
    var blueprintFilter by mutableStateOf(BlueprintFilter.ALL)

    @Stable
    val itemsFiltered by derivedStateOf {
        blueprintsGrouped.mapValues { (type, list) ->
            transaction {
                val listToSearch = when (blueprintFilter) {
                    BlueprintFilter.ALL -> list
                    BlueprintFilter.ACT -> {
                        if (!type.isCustom) list
                        else list.filter { blueprint -> act in blueprint.associatedAct }
                    }
                    BlueprintFilter.TAG -> {
                        if (!type.isCustom) list
                        else transaction {
                            val actTags = act.tags.toSet()

                            list.filter { blueprint -> blueprint.tags.any { it in actTags } }
                        }
                    }
                }

                listToSearch.filter { it.realName.contains(searchString, ignoreCase = true) }
            }
        }
    }

    var connectionState: ConnectionState by mutableStateOf(Disconnected)
        private set

    private var shareSceneJob: Job? = null

    private var unsortedElements by mutableStateOf(transaction { currentScene.elements })

    /**
     * These are all the [Blueprint] placed on  the current map
     */
    val elements by derivedStateOf { unsortedElements.sortedBy { it.priority } }

    @Stable
    val backgroundImage: ImageBitmap by derivedStateOf {
        transaction {
            ImageIO.read(inputStreamFromString(currentScene.background)).also { image ->
                sendMessageToShareScene {
                    val color =
                        if (Settings.labelState == SerializableLabelState.FOR_BOTH) Settings.labelColor.contentColor.toTriple() else null
                    NewMap(
                        Base64Image(image, 1600, 900),
                        elements.filter { it.isVisible }.map { it.toShareSceneToken(color) })
                }
            }.toComposeImageBitmap()
        }
    }

    var cursor by mutableStateOf<Offset?>(null)
        private set

    @JvmName("update cursor state")
    fun setCursor(cursor: Offset?) {
        sendMessageToShareScene {
            if (cursor == null || !Settings.cursorEnabled) CursorHidden else {
                val (cursorColor, borderCursorColor) = Settings.cursorColor

                CursorMoved(
                    Position(cursor.x.toInt(), cursor.y.toInt()),
                    cursorColor.toTriple(),
                    borderCursorColor.toTriple()
                )
            }
        }

        this.cursor = cursor
    }

    /**
     * Returns true if there is at least one element at the given position
     */
    fun hasElementAtPosition(position: Offset) = elements.getTokenFromPosition(position) != null

    fun selectElementsAtPosition(position: Offset, addToExistingElements: Boolean = false) {
        if (!addToExistingElements) {
            selectedElements = elements.getTokenFromPosition(position)?.let { listOf(it) } ?: emptyList()
        } else {
            elements.getTokenFromPosition(position)?.let { selectedElements = selectedElements + it }
        }
        refreshView()
    }

    /**
     * Changes tokens position without dropping them (a moved token stays selected), intended for small steps
     */
    fun moveTokensTo(point: Offset, from: Offset? = null) {
        val origin = from?.let { pos ->
            selectedElements.find { it.hitBox in pos }
                ?: selectElementsAtPosition(pos).let { selectedElements.firstOrNull() }
        }

        /**
         * Return a new [Offset] inside the borders of the map
         */
        fun Offset.checkBoundOf(element: Element?): Offset {
            var newPosition = this

            val height: Int
            val width: Int

            element?.hitBox.let {
                height = (it?.height ?: 0) / 2
                width = (it?.width ?: 0) / 2
            }

            if (newPosition.x < 0) {
                newPosition = newPosition.copy(x = 0f + width)
            } else if (newPosition.x > ABSOLUTE_WIDTH) {
                newPosition = newPosition.copy(x = ABSOLUTE_WIDTH - width)
            }

            if (newPosition.y < 0) {
                newPosition = newPosition.copy(y = 0f + height)
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

                val diffPosition = newPosition - originElement.centerOffset

                val elementToPoint =
                    mapOf(originElement to originElement.positionOf(newPosition)) + selectedElements.filterNot { it === originElement }
                        .map { it to it.positionOf((it.centerOffset + diffPosition).checkBoundOf(it)) }

                currentScene.callCommandManager(elementToPoint, Element::cmdPosition)
            }
        }

        refreshView()
    }

    fun selectAllElements() {
        selectedElements = elements

        refreshView()
    }

    fun selectElements(rec: Rect, getRelativeRect: (Element) -> Rect) {
        selectedElements = buildList {
            elements.forEach {
                if (getRelativeRect(it) in rec) {
                    add(it)
                }
            }
        }

        refreshView()
    }

    private fun unselectElements() {
        selectedElements = emptyList()
        refreshView()
    }

    fun select(up: Boolean = true) = transaction {
        selectedElements = if (selectedElements.isEmpty() && elements.isNotEmpty()) {
            listOf(elements.first())
        } else {
            val operation = if (up) fun Int.(list: List<Element>): Int {
                return if (this == list.size - 1) 0 else this + 1
            } else fun Int.(list: List<Element>): Int {
                return if (this == 0) list.size - 1 else this - 1
            }

            selectedElements.doIfContainsSingle(emptyList()) { blueprint ->
                val elements = elements

                if (elements.getOrNull(elements.indexOfFirst { it.id == blueprint.id }.operation(elements)) != null) {
                    listOf(elements[elements.indexOfFirst { it.id == blueprint.id }.operation(elements)])
                } else emptyList()
            }
        }
    }

    fun rotateRight() {
        Element.cmdOrientationToRight(commandManager, selectedElements)
        refreshView()
    }

    fun rotateLeft() {
        Element.cmdOrientationToLeft(commandManager, selectedElements)
        refreshView()
    }

    /**
     * Remove all elements given as parameter. If no parameter is provided, remove all selected elements
     */
    fun removeElements(elements: List<Element> = selectedElements) {
        commandManager += object : Command {
            override val label = StringLocale[STR_DELETE_SELECTED_TOKENS]

            override fun exec(): Unit = transaction {
                elements.forEach {
                    it.isDeleted = true
                }

                unsortedElements = this@MasterViewModel.elements.toMutableList().also {
                    it -= elements.toSet()
                }

                refreshView()
            }

            override fun cancelExec() = transaction {
                elements.forEach {
                    it.isDeleted = false
                }

                unsortedElements = this@MasterViewModel.elements.toMutableList().also {
                    it += elements.toSet()
                }

                unselectElements()
            }
        }
    }

    fun addNewElement(blueprint: Blueprint) = scope.launch {
        currentScene.addElement(blueprint = blueprint, onAdded = { newElement ->
            unsortedElements = elements.toMutableList().also {
                it += newElement
            }

            selectedElements = listOf(newElement)
        }, onCanceled = { elementToRemove ->
            this@MasterViewModel.unsortedElements = this@MasterViewModel.elements.toMutableList().also {
                it -= elementToRemove
            }
            unselectElements()
            refreshView()
        })

        refreshView()
    }

    fun switchScene(scene: Scene) {
        this.currentScene = scene
        selectedElements = emptyList()
        refreshView(reloadTokens = true)
    }

    fun showBlueprintEditor() {
        selectedElements = emptyList()

        blueprintEditorDialogVisible = true
    }

    fun hideBlueprintEditor() {
        blueprintEditorDialogVisible = false

        blueprintsGrouped = loadBlueprints()

        refreshView(reloadTokens = true)
    }

    fun moveElementsFromScene(elements: List<Element>) {
        Scene.moveElementToScene(currentScene, elements)

        this.unsortedElements = this.elements.toMutableList().also {
            it += elements
        }

        refreshView()
    }

    fun changePriority(newLayer: Layer) = scope.launch(Dispatchers.IO) {
        unsortedElements = elements.onEach {
            if (it in selectedElements) {
                it.priority = newLayer
            }
        }.sortedBy { it.priority }

        withContext(Dispatchers.Main) {
            refreshView()
        }
    }

    fun refreshView(reloadTokens: Boolean = false) = scope.launch {
        withContext(Dispatchers.IO) {
            if (reloadTokens) unsortedElements = newSuspendedTransaction { currentScene.elements }

            sendMessageToShareScene {
                val color =
                    if (Settings.labelState == SerializableLabelState.FOR_BOTH) Settings.labelColor.contentColor.toTriple() else null
                TokenStateChanged(elements.filter { it.isVisible }.map { it.toShareSceneToken(color) })
            }
        }
    }

    fun connectToServer() {
        connectionState = Login

        shareSceneJob = scope.launch(Dispatchers.IO) {
            initWebsocket(
                client = socketClient,
                serverAddress = Preferences.oleboUrl,
                path = "share-scene",
                onFailure = { error ->
                    connectionState = Disconnected.ConnectionFailed(error)
                },
                socketBlock = { manager, setSessionCode ->
                    try {
                        val connectedState = Connected(manager)

                        for (frame in incoming) {
                            when (frame) {
                                is Frame.Close -> {
                                    val closeReason = frame.readReason()

                                    if (closeReason?.knownReason == CloseReason.Codes.NORMAL) {
                                        triggerError(ConnectionError.ServerError(closeReason.message))
                                    }
                                }

                                is Frame.Text -> when (val message = frame.getMessageOrNull()) {
                                    is NewSessionCreated -> {
                                        if (message.minimalOleboVersion > OLEBO_VERSION_CODE) {
                                            triggerError(ConnectionError.WrongVersion)
                                        }

                                        setSessionCode(message.code)

                                        val color =
                                            if (Settings.labelState == SerializableLabelState.FOR_BOTH) Settings.labelColor.contentColor.toTriple() else null

                                        send(NewMap(Base64Image(backgroundImage.toAwtImage(), 1600, 900),
                                            elements.filter { it.isVisible }.map { it.toShareSceneToken(color) })
                                        )

                                        launch {
                                            for (messageToSend in connectedState.shareSceneViewModel.messages) {
                                                send(messageToSend)
                                            }
                                        }

                                        connectionState = connectedState
                                    }

                                    is PlayerAddedOrRemoved -> {
                                        connectedState.shareSceneViewModel.connectedPlayers = message.users
                                    }

                                    else -> continue
                                }

                                else -> continue
                            }
                        }
                    } finally {
                        withContext(Dispatchers.IO) {
                            connectionState = Disconnected
                            manager.close()
                        }
                    }
                })
        }
    }

    fun disconnectFromServer() {
        shareSceneJob?.cancel()
        shareSceneJob = null
    }

    private fun loadBlueprints(): Map<TypeElement, List<Blueprint>> = transaction {
        val items = Blueprint.all().groupBy { it.type }

        (TypeElement.entries + items.keys).associateWith { items[it] ?: emptyList() }
    }

    private fun Element.toShareSceneToken(rgbTooltip: Triple<Int, Int, Int>?) = Token(
        image = Base64Image(sprite.toAwtImage(), size.value),
        rotation = Angle(orientation),
        position = Position(referenceOffset.x.toInt(), referenceOffset.y.toInt()),
        size = size.value,
        label = rgbTooltip?.let { Label(alias, it) }
    )

    private inline fun sendMessageToShareScene(crossinline message: () -> Message) =
        (connectionState as? Connected)?.let { connectedState ->
            scope.launch(Dispatchers.Default) {
                connectedState.shareSceneViewModel.messages.trySend(message())
            }
        }

    private fun Color.toTriple(): Triple<Int, Int, Int> {
        val (r, g, b) = this
        return Triple((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt())
    }

    companion object {
        const val ABSOLUTE_WIDTH = 1600f
        const val ABSOLUTE_HEIGHT = 900f
    }
}