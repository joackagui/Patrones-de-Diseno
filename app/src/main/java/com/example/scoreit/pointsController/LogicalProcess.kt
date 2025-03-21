package com.example.scoreit.pointsController

import com.example.scoreit.components.Cup
import com.example.scoreit.components.Match

class LogicalProcess {
    private var match: Match
    private var cup: Cup

    var totalFirstTeamPoints = 0
    var totalSecondTeamPoints = 0
    var firstTeamPoints = 0
    var secondTeamPoints = 0
    var firstTeamRounds = 0
    var secondTeamRounds = 0
    var requiredDifference = 0
    var requiredPoints = 1000
    var requiredRounds = 1

    constructor(match: Match, cup: Cup) {
        this.match = match
        this.cup = cup
        setData()
    }

    fun setData() {
        requiredDifference = when {
            cup.twoPointsDifference -> 2
            cup.alwaysWinner -> 1
            else -> 0
        }
        requiredPoints = cup.requiredPoints
        requiredRounds = cup.requiredRounds ?: 1
    }

    fun resetPoints() {
        totalFirstTeamPoints += firstTeamPoints
        totalSecondTeamPoints += secondTeamPoints
        firstTeamPoints = 0
        secondTeamPoints = 0
    }
}
