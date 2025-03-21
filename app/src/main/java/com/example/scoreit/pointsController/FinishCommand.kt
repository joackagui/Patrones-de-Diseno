package com.example.scoreit.pointsController

import com.example.scoreit.databinding.ActivityRefereeButtonsBinding

class FinishCommand : ICommand {
    private var binding: ActivityRefereeButtonsBinding
    private var pointsManager: PointsManager

    constructor(binding: ActivityRefereeButtonsBinding, pointsManager: PointsManager) {
        this.binding = binding
        this.pointsManager = pointsManager
    }

    override fun execute(): Int {
        var winner = 0
        if (pointsManager.requiredRounds == 1) {
            pointsManager.totalFirstTeamPoints = pointsManager.firstTeamPoints
            pointsManager.totalSecondTeamPoints = pointsManager.secondTeamPoints
            if (pointsManager.firstTeamPoints > pointsManager.secondTeamPoints) {
                winner = 1
            } else if (pointsManager.secondTeamPoints > pointsManager.firstTeamPoints) {
                winner = 2
            }
            return if (pointsManager.firstTeamPoints - pointsManager.secondTeamPoints >= pointsManager.requiredDifference || pointsManager.secondTeamPoints - pointsManager.firstTeamPoints >= pointsManager.requiredDifference) {
                winner
            } else {
                if (pointsManager.requiredDifference == 1) 10 else 11
            }
        } else {
            if (pointsManager.firstTeamRounds > pointsManager.secondTeamRounds) {
                winner = 1
            } else if (pointsManager.secondTeamRounds > pointsManager.firstTeamRounds) {
                winner = 2
            }
            if (pointsManager.firstTeamRounds == pointsManager.requiredRounds || pointsManager.secondTeamRounds == pointsManager.requiredRounds) {
                return winner
            }
        }
        return -1
    }

    override fun undo() {}
}