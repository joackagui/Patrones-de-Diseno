package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreit.ActivityMainMenu.Companion.ID_USER_MM
import com.example.scoreit.ActivityNewCupSettings.Companion.ID_USER_NC
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
    }

    private fun AppCompatActivity.setHeader() {
//        val userButton = findViewById<Button>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)
//
//        userButton.setOnClickListener {
//            val intent = Intent(this, ActivityLogIn::class.java)
//            startActivity(intent)
//        }
//
        scoreItCup.setOnClickListener {
            val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
            activityMainMenu.putExtra(ID_USER_MM, intent.getStringExtra(ID_USER_NC))
            startActivity(activityMainMenu)
        }
    }

    private fun backButton() {
        val idCup = intent.getStringExtra(ID_CUP_CI)
        if (idCup != null) {
            binding.backButton.setOnClickListener {
                var idUser: String
                lifecycleScope.launch {
                    idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    changeToMainMenu(idUser)
                }
            }
        }
    }

    private fun setCupData() {
        val idCup = intent.getStringExtra(ID_CUP_CI)
        if (idCup != null) {
            lifecycleScope.launch {
                val cup = dbAccess.cupDao().getCupById(idCup)
                val cupName = "${binding.cupName.text} ${cup.name}"
                val gameMode = "${binding.gameModeText.text} ${cup.gameMode}"
                val doubleMatch = "${binding.doubleMatchText.text} ${cup.doubleMatch}"
                val alwaysWinner = "${binding.alwaysWinnerText.text} ${cup.alwaysWinner}"

                binding.cupName.text = cupName
                binding.gameModeText.text = gameMode
                binding.doubleMatchText.text = doubleMatch
                binding.alwaysWinnerText.text = alwaysWinner

                val points = "${binding.pointsText.text} ${cup.winningPoints}"
                if(cup.winningPoints != null){
                    binding.pointsText.text = points
                } else {
                    binding.pointsText.visibility = View.GONE
                }

                val time = "${binding.timeText.text} ${cup.finishTime}"
                if(cup.finishTime != null){
                    binding.timeText.text = time
                } else {
                    binding.timeText.visibility = View.GONE
                }

                val rounds = "${binding.roundsText.text} ${cup.roundsAmount}"
                if(cup.roundsAmount != null){
                    binding.roundsText.text = rounds
                } else {
                    binding.roundsText.visibility = View.GONE
                }

                val restTime = "${binding.restingText.text} ${cup.restingAmount} of ${cup.restingTime} minutes"
                if(cup.restingTime != null && cup.restingAmount != null){
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

                binding.recyclerPartidosCreados.apply {
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

    private fun changeToMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ID_USER_MM, idUser)
        startActivity(activityMainMenu)
    }
}