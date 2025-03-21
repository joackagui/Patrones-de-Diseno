package com.example.scoreit.pointsController

interface ICommand {
    fun execute(): Int

    fun undo()
}