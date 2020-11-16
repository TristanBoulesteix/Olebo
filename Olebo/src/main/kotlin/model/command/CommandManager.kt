package model.command

class CommandManager private constructor() {
    companion object {
        private val managers = mutableMapOf<Int, CommandManager>()

        operator fun invoke(sceneId: Int) = managers[sceneId] ?: CommandManager().also { managers[sceneId] = it }

        operator fun get(sceneId: Int) = managers[sceneId]
    }

    private val stack = mutableListOf<Command>()

    private var pointer = -1

    val label
        get() = stack.getOrNull(pointer)?.label ?: ""

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