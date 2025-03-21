package com.example.scoreit.pointsController

import java.util.Stack

class Controller : ICommand {
    private val commandHistory = Stack<ICommand>()

    fun setController(command: ICommand) {
        commandHistory.push(command)
    }

    override fun execute(): Int {
        if (commandHistory.isNotEmpty()) {
            return commandHistory.peek().execute()
        }
        return 0
    }

    override fun undo() {
        if (commandHistory.isNotEmpty()) {
            commandHistory.pop().undo()
        }
    }
}
