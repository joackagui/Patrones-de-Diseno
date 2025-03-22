package com.example.scoreit.pointsController

import com.example.scoreit.components.Cup
import com.example.scoreit.components.Match

// Clase PointsManager que gestiona los puntos y rondas de un partido
class PointsManager {
    // Partido actual
    private var match: Match

    // Copa asociada al partido
    private var cup: Cup

    // Puntos totales del primer equipo
    var totalFirstTeamPoints = 0

    // Puntos totales del segundo equipo
    var totalSecondTeamPoints = 0

    // Puntos actuales del primer equipo en la ronda actual
    var firstTeamPoints = 0

    // Puntos actuales del segundo equipo en la ronda actual
    var secondTeamPoints = 0

    // Rondas ganadas por el primer equipo
    var firstTeamRounds = 0

    // Rondas ganadas por el segundo equipo
    var secondTeamRounds = 0

    // Diferencia de puntos requerida para ganar una ronda
    var requiredDifference = 0

    // Puntos requeridos para ganar el partido
    var requiredPoints = 1000

    // Rondas requeridas para ganar el partido
    var requiredRounds = 1

    // Constructor que recibe el partido y la copa asociada
    constructor(match: Match, cup: Cup) {
        this.match = match
        this.cup = cup
        setData() // Configurar los datos iniciales
    }

    // Función para configurar los datos iniciales basados en las reglas de la copa
    fun setData() {
        requiredDifference = when {
            cup.twoPointsDifference -> 2 // Si se requiere una diferencia de dos puntos
            cup.alwaysWinner -> 1 // Si siempre debe haber un ganador
            else -> 0 // Sin diferencia requerida
        }
        requiredPoints = cup.requiredPoints // Puntos requeridos para ganar
        requiredRounds = cup.requiredRounds ?: 1 // Rondas requeridas para ganar (por defecto 1)
    }

    // Función para reiniciar los puntos de la ronda actual y sumarlos a los totales
    fun resetPoints() {
        totalFirstTeamPoints += firstTeamPoints // Sumar los puntos actuales del primer equipo a los totales
        totalSecondTeamPoints += secondTeamPoints // Sumar los puntos actuales del segundo equipo a los totales
        firstTeamPoints = 0 // Reiniciar los puntos del primer equipo para la nueva ronda
        secondTeamPoints = 0 // Reiniciar los puntos del segundo equipo para la nueva ronda
    }
}