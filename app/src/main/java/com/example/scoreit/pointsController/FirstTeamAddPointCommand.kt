package com.example.scoreit.pointsController

import com.example.scoreit.databinding.ActivityRefereeButtonsBinding
import kotlin.math.absoluteValue

class FirstTeamAddPointCommand : ICommand {
    private var binding: ActivityRefereeButtonsBinding
    private var pointsManager: PointsManager

    constructor(binding: ActivityRefereeButtonsBinding, pointsManager: PointsManager) {
        this.binding = binding
        this.pointsManager = pointsManager
    }

    override fun execute(): Int {
        if (pointsManager.requiredRounds == 1) {
            val firstTeamReachedGoal = pointsManager.firstTeamPoints >= pointsManager.requiredPoints &&
                    (pointsManager.firstTeamPoints - pointsManager.secondTeamPoints) >= pointsManager.requiredDifference
            val secondTeamReachedGoal = pointsManager.secondTeamPoints >= pointsManager.requiredPoints &&
                    (pointsManager.secondTeamPoints - pointsManager.firstTeamPoints) >= pointsManager.requiredDifference

            if (firstTeamReachedGoal || secondTeamReachedGoal) {
                return 0
            }

            pointsManager.firstTeamPoints++
            binding.firstTeamPoints.text = pointsManager.firstTeamPoints.toString()
        } else {
            if (pointsManager.firstTeamRounds < pointsManager.requiredRounds &&
                pointsManager.secondTeamRounds < pointsManager.requiredRounds) {

                val firstTeamReachedGoal = pointsManager.firstTeamPoints >= pointsManager.requiredPoints &&
                        (pointsManager.firstTeamPoints - pointsManager.secondTeamPoints) >= pointsManager.requiredDifference
                val secondTeamReachedGoal = pointsManager.secondTeamPoints >= pointsManager.requiredPoints &&
                        (pointsManager.secondTeamPoints - pointsManager.firstTeamPoints) >= pointsManager.requiredDifference

                if (firstTeamReachedGoal || secondTeamReachedGoal) {
                    return 0
                }

                pointsManager.firstTeamPoints++
                if (pointsManager.firstTeamPoints >= pointsManager.requiredPoints &&
                    (pointsManager.firstTeamPoints - pointsManager.secondTeamPoints) >= pointsManager.requiredDifference) {

                    pointsManager.firstTeamRounds++
                    pointsManager.resetPoints()

                    binding.firstTeamPoints.text = "0"
                    binding.secondTeamPoints.text = "0"
                    if (pointsManager.firstTeamRounds <= pointsManager.requiredRounds) {
                        val firstTeamRoundsText = "(${pointsManager.firstTeamRounds})"
                        binding.firstTeamRounds.text = firstTeamRoundsText
                    }
                } else {
                    binding.firstTeamPoints.text = pointsManager.firstTeamPoints.toString()
                }
            }
        }
        return 0
    }


    override fun undo() {
        if (pointsManager.firstTeamPoints > 0) {
            pointsManager.firstTeamPoints--
            binding.firstTeamPoints.text = pointsManager.firstTeamPoints.toString()
        } else if (pointsManager.firstTeamRounds > 0) {
            pointsManager.firstTeamRounds--
            val newRounds = "(${pointsManager.firstTeamRounds})"
            binding.firstTeamRounds.text = newRounds
        }
    }
}
