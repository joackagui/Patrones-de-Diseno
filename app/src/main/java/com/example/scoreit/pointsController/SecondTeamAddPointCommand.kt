package com.example.scoreit.pointsController

import com.example.scoreit.databinding.ActivityRefereeButtonsBinding

class SecondTeamAddPointCommand : ICommand {
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

            pointsManager.secondTeamPoints++
            binding.secondTeamPoints.text = pointsManager.secondTeamPoints.toString()

        } else {
            if (pointsManager.secondTeamRounds < pointsManager.requiredRounds &&
                pointsManager.firstTeamRounds < pointsManager.requiredRounds) {

                val firstTeamReachedGoal = pointsManager.firstTeamPoints >= pointsManager.requiredPoints &&
                        (pointsManager.firstTeamPoints - pointsManager.secondTeamPoints) >= pointsManager.requiredDifference
                val secondTeamReachedGoal = pointsManager.secondTeamPoints >= pointsManager.requiredPoints &&
                        (pointsManager.secondTeamPoints - pointsManager.firstTeamPoints) >= pointsManager.requiredDifference

                if (firstTeamReachedGoal || secondTeamReachedGoal) {
                    return 0
                }

                pointsManager.secondTeamPoints++
                if (pointsManager.secondTeamPoints >= pointsManager.requiredPoints &&
                    (pointsManager.secondTeamPoints - pointsManager.firstTeamPoints) >= pointsManager.requiredDifference) {

                    pointsManager.secondTeamRounds++
                    pointsManager.resetPoints()

                    binding.firstTeamPoints.text = "0"
                    binding.secondTeamPoints.text = "0"
                    if (pointsManager.secondTeamRounds <= pointsManager.requiredRounds) {
                        val secondTeamRoundsText = "(${pointsManager.secondTeamRounds})"
                        binding.secondTeamRounds.text = secondTeamRoundsText
                    }
                } else {
                    binding.secondTeamPoints.text = pointsManager.secondTeamPoints.toString()
                }
            }
        }
        return 0
    }


    override fun undo() {
        if (pointsManager.secondTeamPoints > 0) {
            pointsManager.secondTeamPoints--
            binding.secondTeamPoints.text = pointsManager.secondTeamPoints.toString()
        } else if (pointsManager.secondTeamRounds > 0) {
            pointsManager.secondTeamRounds--
            val newRounds = "(${pointsManager.secondTeamRounds})"
            binding.secondTeamRounds.text = newRounds
        }
    }
}