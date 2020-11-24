package model.command

class CommandManager private constructor() {
    companion object {
        private var managerInstance: Pair<Int, CommandManager>? = null

        operator fun invoke(sceneId: Int): CommandManager {
            managerInstance?.let {
                if (it.first == sceneId)
                    return it.second
            }

            return CommandManager().also { managerInstance = sceneId to it }
        }
    }

    private val stack = mutableListOf<Command>()

    val undoLabel
        get() = stack.getOrNull(pointer)?.label

    val redoLabel
        get() = stack.getOrNull(pointer + 1)?.label

    private var pointer = -1

    operator fun plusAssign(command: Command) {
        if (stack.size >= 1) {
            for (i in stack.size - 1 downTo pointer + 1) {
                stack.removeAt(i)
            }
        }

        command.exec()

        stack += command

        pointer++
    }

    fun undo() {
        stack[pointer].cancelExec()
        pointer--
    }

    fun redo() {
        if (pointer == stack.size - 1)
            return
        pointer++
        stack[pointer].exec()
    }
}