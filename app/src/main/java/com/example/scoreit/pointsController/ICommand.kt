package com.example.scoreit.pointsController

// Interfaz ICommand que define los métodos que deben implementar los comandos
interface ICommand {

    // Se ejecuta la acción del comando y devuelve un código de resultado
    fun execute(): Int

    // Se deshace la acción realizada por el comando
    fun undo()
}