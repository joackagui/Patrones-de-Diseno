package com.example.scoreit.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.components.Match
import com.example.scoreit.components.Team
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityRefereeButtonsBinding
import com.example.scoreit.pointsController.Controller
import com.example.scoreit.pointsController.FinishCommand
import com.example.scoreit.pointsController.FirstTeamAddPointCommand
import com.example.scoreit.pointsController.PointsManager
import com.example.scoreit.pointsController.SecondTeamAddPointCommand
import kotlinx.coroutines.launch

class ActivityRefereeButtons : AppCompatActivity() {

    private lateinit var binding: ActivityRefereeButtonsBinding
    private lateinit var dbAccess: AppDataBase
    private lateinit var controller: Controller
    private lateinit var pointsManager: PointsManager
    private lateinit var firstTeamAddPointCommand: FirstTeamAddPointCommand
    private lateinit var secondTeamAddPointCommand: SecondTeamAddPointCommand
    private lateinit var finishCommand: FinishCommand

    companion object {
        const val ID_MATCH_RB: String = "ID_MATCH"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRefereeButtonsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbAccess = getDatabase(this)

        setCommands()
        setButtons()
        setCupData()

        addPointButtons()
        undoButtons()
        finishButton()
    }

    private fun setCommands() {
        lifecycleScope.launch {
            val idMatch = intent.getStringExtra(ID_MATCH_RB) ?: return@launch
            val match = dbAccess.matchDao().getMatchById(idMatch)
            val cup = dbAccess.cupDao().getCupById(match.idCup.toString())

            controller = Controller()
            pointsManager = PointsManager(match, cup)
            firstTeamAddPointCommand = FirstTeamAddPointCommand(binding, pointsManager)
            secondTeamAddPointCommand = SecondTeamAddPointCommand(binding, pointsManager)
            finishCommand = FinishCommand(binding, pointsManager)
        }
    }


    private fun finishButton() {
        binding.finishButton.setOnClickListener {
            lifecycleScope.launch {
                val idMatch = intent.getStringExtra(ID_MATCH_RB)
                if (idMatch != null) {
                    val match = dbAccess.matchDao().getMatchById(idMatch)
                    val cup = dbAccess.cupDao().getCupById(match.idCup.toString())
                    val idFirstTeam = match.idFirstTeam
                    val idSecondTeam = match.idSecondTeam
                    val firstTeam = dbAccess.teamDao().getTeamById(idFirstTeam.toString())
                    val secondTeam = dbAccess.teamDao().getTeamById(idSecondTeam.toString())
                    controller.setCommand(finishCommand)
                    var message: Int = controller.execute()
                    if (message < 10) {
                        if (message == -1) {
                            errorMessage(0)
                        } else {
                            update(match, firstTeam, secondTeam, message)
                            changeToActivityCupInsight(cup.id.toString())
                        }
                    } else {
                        if (message == 10) {
                            errorMessage(1)
                        } else if (message == 11) {
                            errorMessage(2)
                        }
                    }
                }
            }
        }
    }

    private fun addPointButtons() {
        binding.firstTeamFrame.setOnClickListener {
            controller.setCommand(firstTeamAddPointCommand)
            controller.execute()
        }
        binding.secondTeamFrame.setOnClickListener {
            controller.setCommand(secondTeamAddPointCommand)
            controller.execute()
        }

    }

    private fun undoButtons() {
        binding.undoButton.setOnClickListener {
            controller.undo()
        }
    }

    private fun setCupData() {
        val idMatch = intent.getStringExtra(ID_MATCH_RB)
        if (idMatch != null) {
            lifecycleScope.launch {
                val match = dbAccess.matchDao().getMatchById(idMatch)
                val idFirstTeam = match.idFirstTeam
                val idSecondTeam = match.idSecondTeam
                val firstTeam = dbAccess.teamDao().getTeamById(idFirstTeam.toString())
                val secondTeam = dbAccess.teamDao().getTeamById(idSecondTeam.toString())
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
            firstTeam.pointsWon += pointsManager.totalFirstTeamPoints
            firstTeam.pointsLost += pointsManager.totalSecondTeamPoints

            secondTeam.pointsWon += pointsManager.totalSecondTeamPoints
            secondTeam.pointsLost += pointsManager.totalFirstTeamPoints

            if (firstTeam.roundsWon != null) {
                firstTeam.roundsWon = firstTeam.roundsWon!! + (pointsManager.firstTeamRounds)
                firstTeam.roundsLost = firstTeam.roundsLost!! + (pointsManager.secondTeamRounds)
            } else {
                firstTeam.roundsWon = null
                firstTeam.roundsLost = null
            }

            if (secondTeam.roundsWon != null) {
                secondTeam.roundsWon = secondTeam.roundsWon!! + (pointsManager.secondTeamRounds)
                secondTeam.roundsLost = secondTeam.roundsLost!! + (pointsManager.firstTeamRounds)
            } else {
                secondTeam.roundsWon = null
                secondTeam.roundsLost = null
            }

            match.playable = false

            match.firstTeamPoints = pointsManager.totalFirstTeamPoints
            match.secondTeamPoints = pointsManager.totalSecondTeamPoints

            if (pointsManager.requiredRounds == 1) {
                match.firstTeamRounds = null
                match.secondTeamRounds = null
            } else {
                match.firstTeamRounds = pointsManager.firstTeamRounds
                match.secondTeamRounds = pointsManager.secondTeamRounds
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

    private fun errorMessage(message: Int) {
        if (message == 12) {
            Toast.makeText(
                this@ActivityRefereeButtons,
                "There was an error",
                Toast.LENGTH_SHORT
            ).show()
        } else if (message == 1) {
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
        activityCupInsight.putExtra(ActivityCupInsight.Companion.ID_CUP_CI, idCup)

        startActivity(activityCupInsight)
        finish()
    }
}