package model.command

abstract class Command {
    abstract fun exec()

    abstract fun cancelExec()
}