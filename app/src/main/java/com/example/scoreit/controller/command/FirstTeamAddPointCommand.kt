package com.example.scoreit.controller.command

import com.example.scoreit.databinding.ActivityRefereeButtonsBinding

// Clase FirstTeamAddPointCommand que implementa la interfaz ICommand
// para manejar la lógica de agregar puntos al primer equipo
class FirstTeamAddPointCommand : ICommand {

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
        // Verificar si el partido se juega a una sola ronda
        if (pointsManager.requiredRounds == 1) {
            // Verificar si el primer equipo ha alcanzado los puntos requeridos con la diferencia necesaria
            val firstTeamReachedGoal = pointsManager.firstTeamPoints >= pointsManager.requiredPoints &&
                    (pointsManager.firstTeamPoints - pointsManager.secondTeamPoints) >= pointsManager.requiredDifference

            // Verificar si el segundo equipo ha alcanzado los puntos requeridos con la diferencia necesaria
            val secondTeamReachedGoal = pointsManager.secondTeamPoints >= pointsManager.requiredPoints &&
                    (pointsManager.secondTeamPoints - pointsManager.firstTeamPoints) >= pointsManager.requiredDifference

            // Si alguno de los equipos ha alcanzado el objetivo, no se pueden agregar más puntos
            if (firstTeamReachedGoal || secondTeamReachedGoal) {
                return 0
            }

            // Incrementar los puntos del primer equipo
            pointsManager.firstTeamPoints++
            // Actualizar la UI con los nuevos puntos del primer equipo
            binding.firstTeamPoints.text = pointsManager.firstTeamPoints.toString()
        } else {
            // Si el partido tiene múltiples rondas
            if (pointsManager.firstTeamRounds < pointsManager.requiredRounds &&
                pointsManager.secondTeamRounds < pointsManager.requiredRounds) {

                // Verificar si el primer equipo ha alcanzado los puntos requeridos con la diferencia necesaria
                val firstTeamReachedGoal = pointsManager.firstTeamPoints >= pointsManager.requiredPoints &&
                        (pointsManager.firstTeamPoints - pointsManager.secondTeamPoints) >= pointsManager.requiredDifference

                // Verificar si el segundo equipo ha alcanzado los puntos requeridos con la diferencia necesaria
                val secondTeamReachedGoal = pointsManager.secondTeamPoints >= pointsManager.requiredPoints &&
                        (pointsManager.secondTeamPoints - pointsManager.firstTeamPoints) >= pointsManager.requiredDifference

                // Si alguno de los equipos ha alcanzado el objetivo, no se pueden agregar más puntos
                if (firstTeamReachedGoal || secondTeamReachedGoal) {
                    return 0
                }

                // Incrementar los puntos del primer equipo
                pointsManager.firstTeamPoints++

                // Verificar si el primer equipo ha ganado la ronda actual
                if (pointsManager.firstTeamPoints >= pointsManager.requiredPoints &&
                    (pointsManager.firstTeamPoints - pointsManager.secondTeamPoints) >= pointsManager.requiredDifference) {

                    // Incrementar las rondas ganadas por el primer equipo
                    pointsManager.firstTeamRounds++
                    // Reiniciar los puntos de la ronda actual
                    pointsManager.resetPoints()

                    // Actualizar la UI con los puntos reiniciados
                    binding.firstTeamPoints.text = "0"
                    binding.secondTeamPoints.text = "0"

                    // Actualizar la UI con las rondas ganadas por el primer equipo
                    if (pointsManager.firstTeamRounds <= pointsManager.requiredRounds) {
                        val firstTeamRoundsText = "(${pointsManager.firstTeamRounds})"
                        binding.firstTeamRounds.text = firstTeamRoundsText
                    }
                } else {
                    // Si no se ha ganado la ronda, actualizar la UI con los puntos actuales
                    binding.firstTeamPoints.text = pointsManager.firstTeamPoints.toString()
                }
            }
        }
        return 0
    }

    // Implementación de la función undo de la interfaz ICommand
    override fun undo() {
        // Si hay puntos en la ronda actual, deshacer el último punto agregado al primer equipo
        if (pointsManager.firstTeamPoints > 0) {
            pointsManager.firstTeamPoints--
            binding.firstTeamPoints.text = pointsManager.firstTeamPoints.toString()
        } else if (pointsManager.firstTeamRounds > 0) {
            // Si no hay puntos en la ronda actual pero hay rondas ganadas, deshacer la última ronda ganada
            pointsManager.firstTeamRounds--
            val newRounds = "(${pointsManager.firstTeamRounds})"
            binding.firstTeamRounds.text = newRounds
        }
    }
}