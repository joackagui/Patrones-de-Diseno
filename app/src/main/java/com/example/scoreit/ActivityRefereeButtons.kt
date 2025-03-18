package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.ActivityCupInsight.Companion.ID_CUP_CI
import com.example.scoreit.components.Match
import com.example.scoreit.components.Team
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.database.Converters
import com.example.scoreit.databinding.ActivityRefereeButtonsBinding
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class ActivityRefereeButtons : AppCompatActivity() {

    private lateinit var binding: ActivityRefereeButtonsBinding
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_MATCH_RB: String = "ID_MATCH"
    }

    private var totalFirstTeamPoints = 0
    private var totalSecondTeamPoints = 0
    private var firstTeamPoints = 0
    private var secondTeamPoints = 0
    private var firstTeamRounds = 0
    private var secondTeamRounds = 0
    private var requiredDifference = 0
    private var requiredPoints = 1000
    private var requiredRounds = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRefereeButtonsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbAccess = getDatabase(this)

        setButtons()
        setCupData()

        addPointButtons()
        finishButton()
    }

    private fun finishButton() {
        binding.finishButton.setOnClickListener {
            lifecycleScope.launch {
                val idMatch = intent.getStringExtra(ID_MATCH_RB)
                if (idMatch != null) {
                    val match = dbAccess.matchDao().getMatchById(idMatch)
                    val cup = dbAccess.cupDao().getCupById(match.idCup.toString())
                    val firstTeamId = Converters().toTeam(match.firstTeamJson).id.toString()
                    val secondTeamId = Converters().toTeam(match.secondTeamJson).id.toString()
                    val firstTeam = dbAccess.teamDao().getTeamById(firstTeamId)
                    val secondTeam = dbAccess.teamDao().getTeamById(secondTeamId)
                    var winner = 0

                    if (requiredRounds == 1) {
                        totalFirstTeamPoints = firstTeamPoints
                        totalSecondTeamPoints = secondTeamPoints
                        if (firstTeamPoints > secondTeamPoints) {
                            winner = 1
                        } else if (secondTeamPoints > firstTeamPoints) {
                            winner = 2
                        }
                        if (firstTeamPoints - secondTeamPoints >= requiredDifference || secondTeamPoints - firstTeamPoints >= requiredDifference) {
                            update(match, firstTeam, secondTeam, winner)
                            changeToActivityCupInsight(cup.id.toString())
                        } else {
                            var message = 1
                            if (requiredDifference == 2) message = 2
                            errorMessage(message)
                        }
                    } else {
                        if (firstTeamRounds > secondTeamRounds) {
                            winner = 1
                        } else if (secondTeamRounds > firstTeamRounds) {
                            winner = 2
                        }
                        if (firstTeamRounds == requiredRounds || secondTeamRounds == requiredRounds) {
                            update(match, firstTeam, secondTeam, winner)
                            changeToActivityCupInsight(cup.id.toString())
                        }
                    }
                }
            }
        }
    }

    private fun setCupData() {
        val idMatch = intent.getStringExtra(ID_MATCH_RB)
        if (idMatch != null) {
            lifecycleScope.launch {
                val idCup = dbAccess.matchDao().getMatchById(idMatch).idCup.toString()
                val cup = dbAccess.cupDao().getCupById(idCup)
                if (cup.twoPointsDifference) {
                    requiredDifference = 2
                } else if (cup.alwaysWinner) {
                    requiredDifference = 1
                }
                requiredPoints = cup.requiredPoints ?: 1000

                if (cup.requiredRounds != null) {
                    requiredRounds = cup.requiredRounds!!
                }

                val match = dbAccess.matchDao().getMatchById(idMatch)
                val firstTeam = Converters().toTeam(match.firstTeamJson)
                val secondTeam = Converters().toTeam(match.secondTeamJson)
                binding.firstTeamNameDisplay.text = firstTeam.name
                binding.secondTeamNameDisplay.text = secondTeam.name

            }
        }
    }

    private fun setButtons() {
        val idMatch = intent.getStringExtra(ID_MATCH_RB)
        if (idMatch != null) {
            lifecycleScope.launch {
                val match = dbAccess.matchDao().getMatchById(idMatch)
                if (match.firstTeamRounds != null && match.secondTeamRounds != null) {
                    binding.firstTeamRounds.visibility = View.VISIBLE
                    binding.secondTeamRounds.visibility = View.VISIBLE
                    val newFirstTeamRounds = "(${match.firstTeamRounds})"
                    val newSecondTeamRounds = "(${match.secondTeamRounds})"
                    binding.firstTeamRounds.text = newFirstTeamRounds
                    binding.secondTeamRounds.text = newSecondTeamRounds
                } else {
                    binding.firstTeamRounds.visibility = View.INVISIBLE
                    binding.secondTeamRounds.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun update(match: Match, firstTeam: Team, secondTeam: Team, winner: Int) {
        lifecycleScope.launch {
            val idCup = match.idCup.toString()
            val cup = dbAccess.cupDao().getCupById(idCup)
            firstTeam.pointsWon += totalFirstTeamPoints
            firstTeam.pointsLost += totalSecondTeamPoints

            secondTeam.pointsWon += totalSecondTeamPoints
            secondTeam.pointsLost += totalFirstTeamPoints

            if (firstTeam.roundsWon != null) {
                firstTeam.roundsWon = firstTeam.roundsWon!! + firstTeamRounds
                firstTeam.roundsLost = firstTeam.roundsLost!! + secondTeamRounds
            } else {
                firstTeam.roundsWon = null
                firstTeam.roundsLost = null
            }

            if (secondTeam.roundsWon != null) {
                secondTeam.roundsWon = secondTeam.roundsWon!! + secondTeamRounds
                secondTeam.roundsLost = secondTeam.roundsLost!! + firstTeamRounds
            } else {
                secondTeam.roundsWon = null
                secondTeam.roundsLost = null
            }

            match.playable = false

            match.firstTeamPoints = totalFirstTeamPoints
            match.secondTeamPoints = totalSecondTeamPoints

            if (requiredRounds == 1) {
                match.firstTeamRounds = null
                match.secondTeamRounds = null
            } else {
                match.firstTeamRounds = firstTeamRounds
                match.secondTeamRounds = secondTeamRounds
            }

            when (winner) {
                1 -> {
                    match.idWinner = firstTeam.id
                    firstTeam.finalPoints += 3
                    firstTeam.matchesWon += 1
                    secondTeam.matchesLost += 1
                }

                2 -> {
                    match.idWinner = secondTeam.id
                    secondTeam.finalPoints += 3
                    secondTeam.matchesWon += 1
                    firstTeam.matchesLost += 1
                }

                0 -> {
                    firstTeam.finalPoints += 1
                    secondTeam.finalPoints += 1
                }
            }
            firstTeam.matchesPlayed++
            secondTeam.matchesPlayed++

            dbAccess.matchDao().update(match)
            dbAccess.teamDao().update(firstTeam)
            dbAccess.teamDao().update(secondTeam)

            if (!cup.hasStarted) {
                cup.hasStarted = true
                dbAccess.cupDao().update(cup)
            }

            val listOfMatches = dbAccess.matchDao().getMatchesByCupId(cup.id.toString())
            val playedMatches = listOfMatches.filter { !it.playable }.size
            val listOfTeams = dbAccess.teamDao().getTeamsByCupId(cup.id.toString())
            if (cup.playableMatches == playedMatches) {
                cup.winner = listOfTeams.sortedWith(
                    compareByDescending<Team> { it.finalPoints }
                        .thenByDescending { (it.pointsWon - it.pointsLost) }
                        .thenBy { it.name }
                )[0].name
                dbAccess.cupDao().update(cup)
            }

        }
    }

    private fun addPointButtons() {
        binding.firstTeamFrame.setOnClickListener {
            addPoint(true)
        }
        binding.secondTeamFrame.setOnClickListener {
            addPoint(false)
        }
    }

    private fun addPoint(isFirstTeam: Boolean) {
        lifecycleScope.launch {
            val idMatch = intent.getStringExtra(ID_MATCH_RB)
            if (idMatch != null) {
                if (isFirstTeam) {
                    if (requiredRounds == 1) {
                        if ((firstTeamPoints < requiredPoints && secondTeamPoints < requiredPoints) || (firstTeamPoints - secondTeamPoints).absoluteValue < requiredDifference) {
                            firstTeamPoints++
                            val newPoints = firstTeamPoints.toString()
                            binding.firstTeamPoints.text = newPoints
                        } else {
                            totalFirstTeamPoints = firstTeamPoints
                            totalSecondTeamPoints = secondTeamPoints
                        }
                    } else {
                        if (firstTeamRounds < requiredRounds && secondTeamRounds < requiredRounds) {
                            firstTeamPoints++
                            if (firstTeamPoints > requiredPoints &&
                                (firstTeamPoints - (secondTeamPoints + 1) > requiredDifference)
                            ) {
                                firstTeamPoints--
                                firstTeamRounds++
                                resetPoints()
                                if (firstTeamRounds <= requiredRounds) {
                                    val newRounds = "($firstTeamRounds)"
                                    binding.firstTeamRounds.text = newRounds
                                }
                            } else {
                                if ((firstTeamPoints <= requiredPoints && secondTeamPoints <= requiredPoints) || firstTeamPoints - secondTeamPoints < requiredDifference) {
                                    val newPoints = firstTeamPoints.toString()
                                    binding.firstTeamPoints.text = newPoints
                                }
                            }
                        }
                    }
                } else {
                    if (requiredRounds == 1) {
                        if ((secondTeamPoints < requiredPoints && firstTeamPoints < requiredPoints) || (firstTeamPoints - secondTeamPoints).absoluteValue < requiredDifference) {
                            secondTeamPoints++
                            val newPoints = secondTeamPoints.toString()
                            binding.secondTeamPoints.text = newPoints
                        }
                    } else {
                        if (secondTeamRounds < requiredRounds && firstTeamRounds < requiredRounds) {
                            secondTeamPoints++
                            if (secondTeamPoints > requiredPoints &&
                                (secondTeamPoints - (firstTeamPoints + 1) > requiredDifference)
                            ) {
                                secondTeamPoints--
                                secondTeamRounds++
                                resetPoints()
                                if (secondTeamRounds <= requiredRounds) {
                                    val newRounds = "($secondTeamRounds)"
                                    binding.secondTeamRounds.text = newRounds
                                }
                            } else {
                                if ((firstTeamPoints <= requiredPoints && secondTeamPoints <= requiredPoints) || secondTeamPoints - firstTeamPoints < requiredDifference) {
                                    val newPoints = secondTeamPoints.toString()
                                    binding.secondTeamPoints.text = newPoints
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun resetPoints() {
        val idMatch = intent.getStringExtra(ID_MATCH_RB)
        if (idMatch != null) {
            totalFirstTeamPoints += firstTeamPoints
            totalSecondTeamPoints += secondTeamPoints
            firstTeamPoints = 0
            secondTeamPoints = 0
            binding.firstTeamPoints.text = "0"
            binding.secondTeamPoints.text = "0"
        }
    }

    private fun errorMessage(message: Int) {
        if (message == 1) {
            Toast.makeText(
                this@ActivityRefereeButtons,
                "There must be a winner",
                Toast.LENGTH_SHORT
            ).show()
        } else if (message == 2) {
            Toast.makeText(
                this@ActivityRefereeButtons,
                "There must be a two point difference",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun changeToActivityCupInsight(idCup: String) {
        val activityCupInsight = Intent(this, ActivityCupInsight::class.java)
        activityCupInsight.putExtra(ID_CUP_CI, idCup)

        startActivity(activityCupInsight)
        finish()
    }
}