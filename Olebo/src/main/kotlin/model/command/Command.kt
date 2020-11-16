package model.command

abstract class Command {
    abstract val label: String

    abstract fun exec()

    abstract fun cancelExec()
}