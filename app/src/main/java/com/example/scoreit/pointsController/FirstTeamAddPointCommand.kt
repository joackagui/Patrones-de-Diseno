package com.example.scoreit.pointsController

import com.example.scoreit.databinding.ActivityRefereeButtonsBinding
import kotlin.math.absoluteValue

class FirstTeamAddPointCommand : ICommand {
    private var binding: ActivityRefereeButtonsBinding
    private var logicalProcess: LogicalProcess

    constructor(binding: ActivityRefereeButtonsBinding, logicalProcess: LogicalProcess) {
        this.binding = binding
        this.logicalProcess = logicalProcess
    }

    override fun execute(): Int {
        if (logicalProcess.requiredRounds == 1) {
            if ((logicalProcess.firstTeamPoints < logicalProcess.requiredPoints && logicalProcess.secondTeamPoints < logicalProcess.requiredPoints) || (logicalProcess.firstTeamPoints - logicalProcess.secondTeamPoints).absoluteValue < logicalProcess.requiredDifference) {
                logicalProcess.firstTeamPoints++
                val newPoints = logicalProcess.firstTeamPoints.toString()
                binding.firstTeamPoints.text = newPoints
            } else {
                logicalProcess.totalFirstTeamPoints = logicalProcess.firstTeamPoints
                logicalProcess.totalSecondTeamPoints = logicalProcess.secondTeamPoints
            }
        } else {
            if (logicalProcess.firstTeamRounds < logicalProcess.requiredRounds && logicalProcess.secondTeamRounds < logicalProcess.requiredRounds) {
                logicalProcess.firstTeamPoints++
                if (logicalProcess.firstTeamPoints > logicalProcess.requiredPoints &&
                    (logicalProcess.firstTeamPoints - (logicalProcess.secondTeamPoints) >= logicalProcess.requiredDifference)
                ) {
                    logicalProcess.firstTeamPoints--
                    logicalProcess.firstTeamRounds++

                    logicalProcess.resetPoints()
                    binding.firstTeamPoints.text = "0"
                    binding.secondTeamPoints.text = "0"
                    if (logicalProcess.firstTeamRounds <= logicalProcess.requiredRounds) {
                        val newRounds = "(${logicalProcess.firstTeamRounds})"
                        binding.firstTeamRounds.text = newRounds
                    }
                } else {
                    if ((logicalProcess.firstTeamPoints <= logicalProcess.requiredPoints && logicalProcess.secondTeamPoints <= logicalProcess.requiredPoints) || logicalProcess.firstTeamPoints - logicalProcess.secondTeamPoints < logicalProcess.requiredDifference) {
                        val newPoints = logicalProcess.firstTeamPoints.toString()
                        binding.firstTeamPoints.text = newPoints
                    }
                }
            }
        }
        return 0
    }

    override fun undo() {
        if (logicalProcess.firstTeamPoints > 0) {
            logicalProcess.firstTeamPoints--
            binding.firstTeamPoints.text = logicalProcess.firstTeamPoints.toString()
        } else if (logicalProcess.firstTeamRounds > 0) {
            logicalProcess.firstTeamRounds--
            val newRounds = "(${logicalProcess.firstTeamRounds})"
            binding.firstTeamRounds.text = newRounds
        }
    }
}
