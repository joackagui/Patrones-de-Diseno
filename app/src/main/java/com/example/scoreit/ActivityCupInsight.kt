package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreit.ActivityChangeUserData.Companion.ID_USER_CUD
import com.example.scoreit.ActivityMainMenu.Companion.ID_USER_MM
import com.example.scoreit.ActivityNewCupSettings.Companion.ID_CUP_NC
import com.example.scoreit.adapters.RecyclerMatches
import com.example.scoreit.adapters.RecyclerScoreBoardRows
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityCupInsightBinding
import kotlinx.coroutines.launch

class ActivityCupInsight : AppCompatActivity() {

    private val recyclerMatches: RecyclerMatches by lazy { RecyclerMatches() }
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
                    if (!cup.hasEnded) {
                        changeToActivityNewCupSettings(idCup)
                    } else {
                        errorMessage()
                    }
                }
            }
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
                val gameMode = "${binding.gameModeText.text} ${cup.gameMode}"
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
                if (cup.requiredPoints != null) {
                    binding.pointsText.text = points
                } else {
                    binding.pointsText.visibility = View.GONE
                }

                val time = "${binding.timeText.text} ${cup.requiredTime}"
                if (cup.requiredTime != null) {
                    binding.timeText.text = time
                } else {
                    binding.timeText.visibility = View.GONE
                }

                val rounds = "${binding.roundsText.text} ${cup.requiredRounds}"
                if (cup.requiredRounds != null) {
                    binding.roundsText.text = rounds
                } else {
                    binding.roundsText.visibility = View.GONE
                }

                val restTime =
                    "${binding.restingText.text} ${cup.restingAmount} of ${cup.restingTime} minutes"
                if (cup.restingTime != null && cup.restingAmount != null) {
                    binding.restingText.text = restTime
                } else {
                    binding.restingText.visibility = View.GONE
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
                val cup = dbAccess.cupDao().getCupById(idCup)
                if (cup.gameMode == "Brackets") {
                    binding.recyclerScoreBoard.visibility = View.GONE
                    findViewById<TextView>(R.id.team_name_score_board).visibility = View.GONE
                    findViewById<TextView>(R.id.matches_played_score_board).visibility = View.GONE
                    findViewById<TextView>(R.id.marches_won_score_board).visibility = View.GONE
                    findViewById<TextView>(R.id.matches_lost_score_board).visibility = View.GONE
                    findViewById<TextView>(R.id.final_points_score_board).visibility = View.GONE
                    findViewById<TextView>(R.id.in_game_points_score_board).visibility = View.GONE
                } else {
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
    }

    private fun errorMessage() {
        Toast.makeText(this, "Cup has already ended", Toast.LENGTH_LONG).show()
    }

    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ID_USER_MM, idUser)
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish()
    }

    private fun changeToActivityChangeUserData(idUser: String) {
        val activityChangeUserData = Intent(this, ActivityChangeUserData::class.java)
        activityChangeUserData.putExtra(ID_USER_CUD, idUser)

        startActivity(activityChangeUserData)
    }

    private fun changeToActivityNewCupSettings(idCup: String) {
        val activityNewCupSettings = Intent(this, ActivityNewCupSettings::class.java)
        activityNewCupSettings.putExtra(ID_CUP_NC, idCup)

        startActivity(activityNewCupSettings)
    }
}