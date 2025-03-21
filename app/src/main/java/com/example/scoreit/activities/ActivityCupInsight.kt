package com.example.scoreit.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreit.R
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityCupInsightBinding
import com.example.scoreit.recyclers.RecyclerMatches
import com.example.scoreit.recyclers.RecyclerScoreBoardRows
import kotlinx.coroutines.launch

class ActivityCupInsight : AppCompatActivity() {

    private lateinit var recyclerMatches: RecyclerMatches
    private val recyclerScoreBoardRows: RecyclerScoreBoardRows by lazy { RecyclerScoreBoardRows() }
    private lateinit var binding: ActivityCupInsightBinding
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_CUP_CI: String = "ID_CUP"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCupInsightBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        dbAccess = getDatabase(this)

        recyclerMatches = RecyclerMatches(dbAccess, lifecycleScope)

        setHeader()
        setCupData()
        setUpRecyclerScoreBoard()
        setUpRecyclerViewMatches()
        backButton()
        editButton()
    }

    private fun AppCompatActivity.setHeader() {
        val userButton = findViewById<ImageView>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)

        userButton.setOnClickListener {
            lifecycleScope.launch {
                val idCup = intent.getStringExtra(ID_CUP_CI)
                if (idCup != null) {
                    val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    changeToActivityChangeUserData(idUser)
                }
            }
        }

        scoreItCup.setOnClickListener {
            val idCup = intent.getStringExtra(ID_CUP_CI)
            if (idCup != null) {
                lifecycleScope.launch {
                    val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    changeToActivityMainMenu(idUser)
                }
            }
        }
    }

    private fun editButton() {
        binding.editButton.setOnClickListener {
            val idCup = intent.getStringExtra(ID_CUP_CI)
            if (idCup != null) {
                lifecycleScope.launch {
                    val cup = dbAccess.cupDao().getCupById(idCup)
                    if (cup.winner != null) {
                        deleteCup(idCup)
                    } else {
                        changeToActivityNewCupSettings(idCup)
                    }
                }
            }
        }
    }

    private fun deleteCup(idCup: String) {
        lifecycleScope.launch {
            dbAccess.matchDao().deleteMatchesByIdCup(idCup)
            dbAccess.teamDao().deleteTeamsByIdCup(idCup)
            dbAccess.cupDao().deleteById(idCup)

            val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
            changeToActivityMainMenu(idUser)
        }
    }

    private fun backButton() {
        val idCup = intent.getStringExtra(ID_CUP_CI)
        if (idCup != null) {
            binding.backButton.setOnClickListener {
                var idUser: String
                lifecycleScope.launch {
                    idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    changeToActivityMainMenu(idUser)
                }
            }
        }
    }

    private fun setCupData() {
        lifecycleScope.launch {
            val idCup = intent.getStringExtra(ID_CUP_CI)
            if (idCup != null) {
                val cup = dbAccess.cupDao().getCupById(idCup)
                val cupName: String = cup.name
                val gameMode = "Round Robin"
                val doubleMatch = "${binding.doubleMatchText.text} ${cup.doubleMatch}"
                val alwaysWinner = "${binding.alwaysWinnerText.text} ${cup.alwaysWinner}"
                val twoPointsDifference =
                    "${binding.twoPointsDifferenceText.text} ${cup.twoPointsDifference}"

                binding.cupName.text = cupName
                binding.gameModeText.text = gameMode
                binding.doubleMatchText.text = doubleMatch
                binding.alwaysWinnerText.text = alwaysWinner
                binding.twoPointsDifferenceText.text = twoPointsDifference

                val points = "${binding.pointsText.text} ${cup.requiredPoints}"
                binding.pointsText.text = points

                val rounds = "${binding.roundsText.text} ${cup.requiredRounds}"
                if (cup.requiredRounds != null) {
                    binding.roundsText.text = rounds
                } else {
                    binding.roundsText.visibility = View.GONE
                }

                val winnerName = cup.winner
                if (winnerName != null) {
                    val deleteText = "Delete"
                    binding.editButton.text = deleteText

                    binding.winnerTag.visibility = View.VISIBLE
                    binding.winnerName.visibility = View.VISIBLE
                    binding.winnerName.text = winnerName
                    finishMessage()
                }
            }
        }
    }

    private fun setUpRecyclerViewMatches() {
        val idCup = intent.getStringExtra(ID_CUP_CI)
        if (idCup != null) {
            lifecycleScope.launch {
                val listOfMatches = dbAccess.matchDao().getMatchesByCupId(idCup).toMutableList()
                recyclerMatches.addDataToList(listOfMatches)

                binding.recyclerMatches.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = recyclerMatches
                    isNestedScrollingEnabled = false
                }
            }
        }
    }

    private fun setUpRecyclerScoreBoard() {
        val idCup = intent.getStringExtra(ID_CUP_CI)
        if (idCup != null) {
            lifecycleScope.launch {
                val listOfTeams =
                    dbAccess.teamDao().getTeamsByCupId(idCup).toMutableList()
                recyclerScoreBoardRows.addDataToList(listOfTeams)

                binding.recyclerScoreBoard.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = recyclerScoreBoardRows
                    isNestedScrollingEnabled = false
                }
            }
        }
    }

    private fun finishMessage() {
        val idCup = intent.getStringExtra(ID_CUP_CI)
        if (idCup != null) {
            lifecycleScope.launch {
                val cup = dbAccess.cupDao().getCupById(idCup)
                val finishMessage = "The ${cup.name} has finished"
                Toast.makeText(this@ActivityCupInsight, finishMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ActivityMainMenu.Companion.ID_USER_MM, idUser)
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish()
    }

    private fun changeToActivityChangeUserData(idUser: String) {
        val activityChangeUserData = Intent(this, ActivityChangeUserData::class.java)
        activityChangeUserData.putExtra(ActivityChangeUserData.Companion.ID_USER_CUD, idUser)

        startActivity(activityChangeUserData)
    }

    private fun changeToActivityNewCupSettings(idCup: String) {
        val activityNewCupSettings = Intent(this, ActivityNewCupSettings::class.java)
        activityNewCupSettings.putExtra(ActivityNewCupSettings.Companion.ID_CUP_NC, idCup)

        startActivity(activityNewCupSettings)
    }
}