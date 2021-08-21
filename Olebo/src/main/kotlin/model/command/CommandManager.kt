package jdr.exia.model.command

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class CommandManager private constructor() : MutableList<Command> by mutableStateListOf() {
    companion object {
        private var managerInstance by mutableStateOf<Pair<Int, CommandManager>?>(null)

        operator fun invoke(sceneId: Int): CommandManager {
            managerInstance?.let {
                if (it.first == sceneId)
                    return it.second
            }

            return CommandManager().also { managerInstance = sceneId to it }
        }

        operator fun invoke(sceneId: EntityID<Int>) = transaction {
            CommandManager(sceneId.value)
        }
    }

    val undoLabel
        get() = getOrNull(pointer)?.label

    val redoLabel
        get() = getOrNull(pointer + 1)?.label

    val hasUndoAction
        get() = getOrNull(pointer) != null

    val hasRedoAction
        get() = getOrNull(pointer + 1) != null

    private var pointer by mutableStateOf(-1)

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
        this[pointer].cancelExec()
        pointer--
    }

    fun redo() {
        if (pointer == this.size - 1)
            return
        pointer++
        this[pointer].exec()
    }
}