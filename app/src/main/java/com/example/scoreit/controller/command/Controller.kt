package com.example.scoreit.controller.command

import java.util.Stack

// Clase Controller que implementa la interfaz ICommand y gestiona la ejecución y deshacer de comandos
class Controller : ICommand {

    // Pila (Stack) para almacenar el historial de comandos ejecutados
    private val commandHistory = Stack<ICommand>()

    // Función para establecer el comando actual y agregarlo al historial
    fun setCommand(command: ICommand) {
        commandHistory.push(command) // Agrega el comando a la pila
    }

    // Implementación de la función execute de la interfaz ICommand
    override fun execute(): Int {
        // Verifica si hay comandos en el historial
        if (commandHistory.isNotEmpty()) {
            // Ejecuta el comando en la cima de la pila y devuelve su resultado
            return commandHistory.peek().execute()
        }
        // Si no hay comandos, devuelve 0
        return 0
    }

    // Implementación de la función undo de la interfaz ICommand
    override fun undo() {
        // Verifica si hay comandos en el historial
        if (commandHistory.isNotEmpty()) {
            // Deshace el último comando ejecutado (el que está en la cima de la pila)
            commandHistory.pop().undo()
        }
    }
}