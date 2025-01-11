package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreit.ActivityChangeUserData.Companion.ID_USER_CUD
import com.example.scoreit.ActivityMainMenu.Companion.ID_USER_MM
import com.example.scoreit.ActivityNewCupSettings.Companion.CUP_JSON_NC
import com.example.scoreit.ActivityNewTeamSettings.Companion.ID_CUP_NT
import com.example.scoreit.adapters.RecyclerTeams
import com.example.scoreit.components.Team
import com.example.scoreit.components.Match
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.database.Converters
import com.example.scoreit.databinding.ActivityAddTeamBinding
import kotlinx.coroutines.launch

class ActivityAddTeam : AppCompatActivity() {

    private val recyclerTeams: RecyclerTeams by lazy { RecyclerTeams() }
    private lateinit var binding: ActivityAddTeamBinding
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_CUP_AT: String = "ID_CUP"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbAccess = getDatabase(this)

        setHeader()
        defaultTeams()

        backButton()
        addButton()
        saveButton()
    }

    private fun AppCompatActivity.setHeader() {
        val userButton = findViewById<ImageView>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)

        userButton.setOnClickListener {
            lifecycleScope.launch {
                val idCup = intent.getStringExtra(ID_CUP_AT)
                if (idCup != null) {
                    val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    deleteCup()
                    changeToActivityChangeUserData(idUser)
                }
            }
        }

        scoreItCup.setOnClickListener {
            lifecycleScope.launch {
                val idCup = intent.getStringExtra(ID_CUP_AT)
                if (idCup != null) {
                    deleteCup()
                    val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    changeToActivityMainMenu(idUser)
                }
            }
        }
    }

    private fun backButton() {
        val idCup = intent.getStringExtra(ID_CUP_AT)
        if (idCup != null) {
            binding.backButton.setOnClickListener {
                lifecycleScope.launch {
                    val lastCup = dbAccess.cupDao().getCupById(idCup)
                    val lastCupJson = Converters().fromCup(lastCup)
                    deleteCup()
                    changeToActivityNewCupSettings(lastCupJson)
                }
            }
        }
    }

    private fun addButton() {
        binding.addTeamButton.setOnClickListener {
            changeToActivityNewTeamSettings()
        }
    }

    private fun saveButton() {
        binding.saveButton.setOnClickListener {
            val idCup = intent.getStringExtra(ID_CUP_AT)
            if (idCup != null) {
                lifecycleScope.launch {
                    if (dbAccess.teamDao().getTeamsByCupId(idCup).size >= 2) {
                        createMatches()
                        val user = dbAccess.userDao()
                            .getUserById(dbAccess.cupDao().getCupById(idCup).idUser.toString())
                        changeToActivityMainMenu(user.id.toString())
                        successfulMessage(true)
                    } else {
                        successfulMessage(false)
                    }
                }
            }
        }
    }

    private fun setTeamsAmount() {
        val idCup = intent.getStringExtra(ID_CUP_AT)
        if (idCup != null) {
            lifecycleScope.launch {
                val teamsAmount = dbAccess.teamDao().getTeamsByCupId(idCup).size.toString()
                val newText = "${binding.teamCounter.text} $teamsAmount"
                binding.teamCounter.text = newText
            }
        }
    }

    private fun deleteCup() {
        val idCup = intent.getStringExtra(ID_CUP_AT)
        if (idCup != null) {
            lifecycleScope.launch {
                dbAccess.matchDao().deleteMatchesByIdCup(idCup)
                dbAccess.teamDao().deleteTeamsByIdCup(idCup)
                dbAccess.cupDao().deleteById(idCup)
            }
        }
    }

    private fun defaultTeams() {
        val idCup = intent.getStringExtra(ID_CUP_AT)
        if (idCup != null) {
            lifecycleScope.launch {
                if (dbAccess.teamDao().getTeamsByCupId(idCup).isEmpty()) {
                    val firstTeam = Team(name = "Team 1", idCup = idCup.toInt())
                    val secondTeam = Team(name = "Team 2", idCup = idCup.toInt())
                    dbAccess.teamDao().insert(firstTeam)
                    dbAccess.teamDao().insert(secondTeam)
                }
                setUpRecyclerView()
                setTeamsAmount()
            }
        }
    }

    private fun successfulMessage(success: Boolean) {
        if (success) {
            Toast.makeText(this, "Creation successful", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "You need at least two teams", Toast.LENGTH_LONG).show()
        }
    }

    private fun createMatches() {
        val idCup = intent.getStringExtra(ID_CUP_AT)
        if (idCup != null) {
            lifecycleScope.launch {
                val cup = dbAccess.cupDao().getCupById(idCup.toString())
                when (cup.gameMode) {
                    "Round Robin" -> {
                        roundRobin()
                    }

                    "Brackets" -> {
                        brackets()
                    }

                    else -> {
                        //roundRobin()
                        //brackets()
                    }
                }
            }
        }
    }

    private fun brackets() {
//        val idCup = intent.getStringExtra(ID_CUP_AT)
//        if(idCup != null){
//            lifecycleScope.launch {
//                val listOfTeams =
//                    dbAccess.teamDao().getTeamsByCupId(idCup.toString()).toMutableList()
//
//                if (listOfTeams.size < 2) {
//                    throw IllegalArgumentException("You need at least two teams")
//                }
//
//                val teamsAmount = listOfTeams.size
//                val nextTwoExponential =
//                    Integer.highestOneBit(teamsAmount).takeIf { it == teamsAmount }
//                        ?: (Integer.highestOneBit(teamsAmount) * 2)
//
//                while (listOfTeams.size < nextTwoExponential) {
//                    listOfTeams.add(
//                        Team(
//                            id = -1,
//                            name = "Team Default",
//                            idCup = idCup.toInt()
//                        )
//                    )
//                }
//
//                val stages = mapOf(
//                    1 to "Final",
//                    2 to "Semi-final",
//                    4 to "Quarter-final",
//                    8 to "Round of 16",
//                    16 to "Round of 32"
//                )
//
//                var rondaEquipos = listOfTeams
//                var rondaActual = nextTwoExponential
//                var stage = 1
//
//                val matches = mutableListOf<Match>()
//
//                while (rondaActual > 1) {
//                    val stageName = stages[rondaActual] ?: "Fase $stage"
//                    val nuevaRonda = mutableListOf<Team>()
//
//                    for (i in 0 until rondaEquipos.size / 2) {
//                        val local = rondaEquipos[i]
//                        val visitante = rondaEquipos[rondaEquipos.size - 1 - i]
//
//                        val localGson = Converters().fromTeam(local)
//                        val visitanteGson = Converters().fromTeam(visitante)
//
//                        matches.add(
//                            Match(
//
//                            )
//                        )
//
//                        nuevaRonda.add(if (local.id != -1) local else visitante)
//                    }
//
//                    rondaEquipos = nuevaRonda
//                    rondaActual /= 2
//                    stage++
//                }
//
//                dbAccess.matchDao().insertMatches(matches)
//
//            }
//        }
    }

    private fun roundRobin() {
        val idCup = intent.getStringExtra(ID_CUP_AT)
        if (idCup != null) {
            lifecycleScope.launch {
                val cup = dbAccess.cupDao().getCupById(idCup.toString())
                val listOfTeams =
                    dbAccess.teamDao().getTeamsByCupId(idCup.toString()).toMutableList()

                if (listOfTeams.size < 2) {
                    throw IllegalArgumentException("You need at least two teams")
                }

                val matches = mutableListOf<Match>()

                val isUneven = listOfTeams.size % 2 != 0
                if (isUneven) {
                    listOfTeams.add(Team(id = -1, name = "Free", idCup = idCup.toInt()))
                }

                val matchDaysAmount = listOfTeams.size - 1
                val teamsHalf = listOfTeams.size / 2

                for (matchDay in 1..matchDaysAmount) {
                    var firstOfKind = true
                    for (i in 0 until teamsHalf) {
                        val firstTeam = listOfTeams[i]
                        val secondTeam = listOfTeams[listOfTeams.size - 1 - i]

                        if (firstTeam.id != -1 && secondTeam.id != -1) {
                            val firstTeamJson = Converters().fromTeam(firstTeam)
                            val secondTeamJson = Converters().fromTeam(secondTeam)

                            var initialRounds: Int? = 0
                            if (cup.roundsAmount == null) {
                                initialRounds = null
                            }
                            matches.add(
                                Match(
                                    matchDay = matchDay,
                                    firstOfKind = firstOfKind,
                                    firstTeamJson = firstTeamJson,
                                    secondTeamJson = secondTeamJson,
                                    firstTeamRounds = initialRounds,
                                    secondTeamRounds = initialRounds,
                                    idCup = idCup.toInt()
                                )
                            )
                            if (cup.doubleMatch) {
                                matches.add(
                                    Match(
                                        matchDay = matchDay + matchDaysAmount,
                                        firstOfKind = firstOfKind,
                                        firstTeamJson = secondTeamJson,
                                        secondTeamJson = firstTeamJson,
                                        firstTeamRounds = initialRounds,
                                        secondTeamRounds = initialRounds,
                                        idCup = idCup.toInt()
                                    )
                                )
                            }
                            if(firstOfKind){
                                firstOfKind = false
                            }
                        }
                    }
                    val mixer = listOfTeams.removeAt(listOfTeams.size - 1)
                    listOfTeams.add(1, mixer)
                }
                dbAccess.matchDao().insertMatches(matches)
            }
        }
    }

    private fun setUpRecyclerView() {
        val idCup = intent.getStringExtra(ID_CUP_AT)
        if (idCup != null) {
            lifecycleScope.launch {
                val listOfTeams = dbAccess.teamDao().getTeamsByCupId(idCup).toMutableList()
                recyclerTeams.addDataToList(listOfTeams)

                binding.recyclerTeams.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = recyclerTeams
                }
            }
        }
    }

    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ID_USER_MM, idUser)
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish()
    }

    private fun changeToActivityNewTeamSettings() {
        val idCup = intent.getStringExtra(ID_CUP_AT)
        if (idCup != null) {
            val activityNewTeamSettings = Intent(this, ActivityNewTeamSettings::class.java)
            activityNewTeamSettings.putExtra(ID_CUP_NT, idCup)

            startActivity(activityNewTeamSettings)
        }
    }

    private fun changeToActivityNewCupSettings(lastCupJson: String) {
        val idCup = intent.getStringExtra(ID_CUP_AT)
        if (idCup != null) {
            val activityNewCupSettings = Intent(this, ActivityNewCupSettings::class.java)
            activityNewCupSettings.putExtra(CUP_JSON_NC, lastCupJson)

            startActivity(activityNewCupSettings)
            finish()
        }
    }

    private fun changeToActivityChangeUserData(idUser: String) {
        val activityChangeUserData = Intent(this, ActivityChangeUserData::class.java)
        activityChangeUserData.putExtra(ID_USER_CUD, idUser)

        startActivity(activityChangeUserData)
    }
}



