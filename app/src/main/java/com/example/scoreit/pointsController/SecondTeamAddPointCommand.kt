package com.example.scoreit.pointsController

import com.example.scoreit.databinding.ActivityRefereeButtonsBinding
import kotlin.math.absoluteValue

class SecondTeamAddPointCommand : ICommand {
    private var binding: ActivityRefereeButtonsBinding
    private var logicalProcess: LogicalProcess

    constructor(binding: ActivityRefereeButtonsBinding, logicalProcess: LogicalProcess) {
        this.binding = binding
        this.logicalProcess = logicalProcess
    }

    override fun execute(): Int {
        if (logicalProcess.requiredRounds == 1) {
            if ((logicalProcess.secondTeamPoints < logicalProcess.requiredPoints && logicalProcess.firstTeamPoints < logicalProcess.requiredPoints) || (logicalProcess.firstTeamPoints - logicalProcess.secondTeamPoints).absoluteValue < logicalProcess.requiredDifference) {
                logicalProcess.secondTeamPoints++
                val newPoints = logicalProcess.secondTeamPoints.toString()
                binding.secondTeamPoints.text = newPoints
            }
        } else {
            if (logicalProcess.secondTeamRounds < logicalProcess.requiredRounds && logicalProcess.firstTeamRounds < logicalProcess.requiredRounds) {
                logicalProcess.secondTeamPoints++
                if (logicalProcess.secondTeamPoints > logicalProcess.requiredPoints &&
                    (logicalProcess.secondTeamPoints - (logicalProcess.firstTeamPoints) >= logicalProcess.requiredDifference)
                ) {
                    logicalProcess.secondTeamPoints--
                    logicalProcess.secondTeamRounds++

                    logicalProcess.resetPoints()
                    binding.firstTeamPoints.text = "0"
                    binding.secondTeamPoints.text = "0"
                    if (logicalProcess.secondTeamRounds <= logicalProcess.requiredRounds) {
                        val newRounds = "(${logicalProcess.secondTeamRounds})"
                        binding.secondTeamRounds.text = newRounds
                    }
                } else {
                    if ((logicalProcess.firstTeamPoints <= logicalProcess.requiredPoints && logicalProcess.secondTeamPoints <= logicalProcess.requiredPoints) || logicalProcess.secondTeamPoints - logicalProcess.firstTeamPoints < logicalProcess.requiredDifference) {
                        val newPoints = logicalProcess.secondTeamPoints.toString()
                        binding.secondTeamPoints.text = newPoints
                    }
                }
            }
        }
        return 0
    }

    override fun undo() {
        if (logicalProcess.secondTeamPoints > 0) {
            logicalProcess.secondTeamPoints--
            binding.secondTeamPoints.text = logicalProcess.secondTeamPoints.toString()
        } else if (logicalProcess.secondTeamRounds > 0) {
            logicalProcess.secondTeamRounds--
            val newRounds = "(${logicalProcess.secondTeamRounds})"
            binding.secondTeamRounds.text = newRounds
        }
    }
}