package jdr.exia.model.command

import androidx.compose.runtime.*
import org.jetbrains.exposed.dao.id.EntityID

@Stable
class CommandManager private constructor() : MutableList<Command> by mutableStateListOf() {
    val undoLabel
        get() = getOrNull(pointer)?.label

    val redoLabel
        get() = getOrNull(pointer + 1)?.label

    val hasUndoAction
        get() = getOrNull(pointer) != null

    val hasRedoAction
        get() = getOrNull(pointer + 1) != null

    private var pointer by mutableStateOf(-1)

    /**
     * Public key to be used as key to recompose Composable that depend on commands
     */
    @Stable
    val composeKey get() = pointer

    operator fun plusAssign(command: Command) {
        if (size >= 1) {
            for (i in size - 1 downTo pointer + 1) {
                removeAt(i)
            }
        }

        command.exec()

        add(command)

        pointer++
    }

    fun undo() {
        if (pointer !in 0 until size)
            return

        this[pointer].cancelExec()
        pointer--
    }

    fun redo() {
        if (pointer == this.size - 1)
            return
        pointer++
        this[pointer].exec()
    }

    companion object {
        private var managerInstance by mutableStateOf<Pair<EntityID<Int>, CommandManager>?>(null)

        operator fun invoke(sceneId: EntityID<Int>): CommandManager {
            managerInstance?.let { (id, manager) ->
                if (id == sceneId)
                    return manager
            }

            return CommandManager().also { managerInstance = sceneId to it }
        }

        fun clear() {
            managerInstance = null
        }
    }
}