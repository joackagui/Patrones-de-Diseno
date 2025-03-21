package com.example.scoreit.pointsController

import com.example.scoreit.databinding.ActivityRefereeButtonsBinding

class FinishCommand : ICommand {
    private var binding: ActivityRefereeButtonsBinding
    private var logicalProcess: LogicalProcess

    constructor(binding: ActivityRefereeButtonsBinding, logicalProcess: LogicalProcess) {
        this.binding = binding
        this.logicalProcess = logicalProcess

    }

    override fun execute(): Int {
        var winner = 0
        if (logicalProcess.requiredRounds == 1) {
            logicalProcess.totalFirstTeamPoints = logicalProcess.firstTeamPoints
            logicalProcess.totalSecondTeamPoints = logicalProcess.secondTeamPoints
            if (logicalProcess.firstTeamPoints > logicalProcess.secondTeamPoints) {
                winner = 1
            } else if (logicalProcess.secondTeamPoints > logicalProcess.firstTeamPoints) {
                winner = 2
            }
            return if (logicalProcess.firstTeamPoints - logicalProcess.secondTeamPoints >= logicalProcess.requiredDifference || logicalProcess.secondTeamPoints - logicalProcess.firstTeamPoints >= logicalProcess.requiredDifference) {
                winner
            } else {
                if (logicalProcess.requiredDifference == 1) 10 else 11
            }
        } else {
            if (logicalProcess.firstTeamRounds > logicalProcess.secondTeamRounds) {
                winner = 1
            } else if (logicalProcess.secondTeamRounds > logicalProcess.firstTeamRounds) {
                winner = 2
            }
            if (logicalProcess.firstTeamRounds == logicalProcess.requiredRounds || logicalProcess.secondTeamRounds == logicalProcess.requiredRounds) {
                return winner
            }
        }
        return -1
    }

    override fun undo() {}
}