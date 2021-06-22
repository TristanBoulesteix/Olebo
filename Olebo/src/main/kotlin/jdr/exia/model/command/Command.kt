package jdr.exia.model.command

interface Command {
    val label: String

    fun exec()

    fun cancelExec()
}