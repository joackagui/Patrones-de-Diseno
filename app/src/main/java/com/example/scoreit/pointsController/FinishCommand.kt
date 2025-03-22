package com.example.scoreit.pointsController

import com.example.scoreit.databinding.ActivityRefereeButtonsBinding

// Clase FinishCommand que implementa la interfaz ICommand
// para manejar la lógica de finalizar un partido y determinar el ganador
class FinishCommand : ICommand {

    // Binding para acceder a los elementos de la UI
    private var binding: ActivityRefereeButtonsBinding

    // Gestor de puntos que maneja la lógica de los puntos y rondas
    private var pointsManager: PointsManager

    // Constructor que recibe el binding y el gestor de puntos
    constructor(binding: ActivityRefereeButtonsBinding, pointsManager: PointsManager) {
        this.binding = binding
        this.pointsManager = pointsManager
    }

    // Implementación de la función execute de la interfaz ICommand
    override fun execute(): Int {
        var winner = 0 // Variable para almacenar el equipo ganador (0: empate, 1: primer equipo, 2: segundo equipo)

        // Verificar si el partido se juega a una sola ronda
        if (pointsManager.requiredRounds == 1) {
            // Asignar los puntos actuales a los totales
            pointsManager.totalFirstTeamPoints = pointsManager.firstTeamPoints
            pointsManager.totalSecondTeamPoints = pointsManager.secondTeamPoints

            // Determinar el equipo ganador basado en los puntos
            if (pointsManager.firstTeamPoints > pointsManager.secondTeamPoints) {
                winner = 1 // Primer equipo gana
            } else if (pointsManager.secondTeamPoints > pointsManager.firstTeamPoints) {
                winner = 2 // Segundo equipo gana
            }

            // Verificar si la diferencia de puntos cumple con el requisito
            return if (pointsManager.firstTeamPoints - pointsManager.secondTeamPoints >= pointsManager.requiredDifference ||
                pointsManager.secondTeamPoints - pointsManager.firstTeamPoints >= pointsManager.requiredDifference
            ) {
                winner // Devolver el equipo ganador si se cumple la diferencia
            } else {
                // Devolver un código de error si no se cumple la diferencia
                if (pointsManager.requiredDifference == 1) 10 else 11
            }
        } else {
            // Si el partido tiene múltiples rondas
            // Determinar el equipo ganador basado en las rondas ganadas
            if (pointsManager.firstTeamRounds > pointsManager.secondTeamRounds) {
                winner = 1 // Primer equipo gana
            } else if (pointsManager.secondTeamRounds > pointsManager.firstTeamRounds) {
                winner = 2 // Segundo equipo gana
            }

            // Verificar si algún equipo ha alcanzado el número de rondas requeridas
            if (pointsManager.firstTeamRounds == pointsManager.requiredRounds || pointsManager.secondTeamRounds == pointsManager.requiredRounds) {
                return winner // Devolver el equipo ganador
            }
        }

        // Si no se ha determinado un ganador, devolver -1 (indicando que el partido no ha terminado)
        return -1
    }

    // Implementación de la función undo de la interfaz ICommand
    // En este caso, no se implementa ninguna lógica para deshacer
    override fun undo() {}
}