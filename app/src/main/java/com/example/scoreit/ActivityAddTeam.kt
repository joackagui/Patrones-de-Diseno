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
import com.example.scoreit.components.Match
import com.example.scoreit.components.Team
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
                val cup = dbAccess.cupDao().getCupById(idCup)
                val rounds = cup.requiredRounds

                if (dbAccess.teamDao().getTeamsByCupId(idCup).isEmpty()) {
                    val firstTeam = Team(
                        name = "Team 1",
                        idCup = idCup.toInt(),
                        roundsWon = rounds,
                        roundsLost = rounds
                    )
                    val secondTeam = Team(
                        name = "Team 2",
                        idCup = idCup.toInt(),
                        roundsWon = rounds,
                        roundsLost = rounds
                    )
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
                        roundRobin(idCup)
                    }

                    "Brackets" -> {
                        brackets(idCup)
                    }

                    else -> {
                        //roundRobin()
                        //brackets()
                    }
                }
            }
        }
    }

    private fun updateCup(idCup: String) {
        lifecycleScope.launch {
            val cup = dbAccess.cupDao().getCupById(idCup)
            val matches = dbAccess.matchDao().getMatchesByCupId(idCup)
            cup.playableMatches = matches.size
            dbAccess.cupDao().update(cup)
        }
    }

    private fun brackets(idCup: String) {
        lifecycleScope.launch {
            val cup = dbAccess.cupDao().getCupById(idCup)
            val listOfTeams =
                dbAccess.teamDao().getTeamsByCupId(idCup).toMutableList()

            if (listOfTeams.size < 2) {
                throw IllegalArgumentException("You need at least two teams")
            }
            val newMatches = mutableListOf<Match>()

            val nextPowerOfTwo =
                1.shl((listOfTeams.size - 1).takeHighestOneBit().countTrailingZeroBits() + 1)
            val placeholdersNeeded =
                nextPowerOfTwo - listOfTeams.size

            val placeholderTeam = Team(
                id = -1,
                name = "Default Team",
                idCup = idCup.toInt()
            )

            val allTeams = listOfTeams.apply {
                repeat(placeholdersNeeded) { add(placeholderTeam) }
            }

            val stage = when (nextPowerOfTwo) {
                2 -> 1 // Final
                4 -> 2 // Semifinal
                8 -> 4 // Quarter-final
                16 -> 8 // Round of 16
                32 -> 16 // Round of 32
                else -> null
            }

            var firstOfKind = true
            var initialRounds: Int? = 0
            if(cup.requiredRounds == null){
                initialRounds = null
            }

            for (i in 0 until allTeams.size / 2) {
                val firstTeam = allTeams[i]
                val secondTeam = allTeams[allTeams.size - 1 - i]

                if (firstTeam.id != -1 && secondTeam.id != -1) {
                    newMatches.add(
                        Match(
                            stage = stage,
                            firstOfKind = firstOfKind,
                            firstTeamJson = Converters().fromTeam(firstTeam),
                            secondTeamJson = Converters().fromTeam(secondTeam),
                            firstTeamRounds = initialRounds,
                            secondTeamRounds = initialRounds,
                            idCup = idCup.toInt(),
                        )
                    )
                    if (firstOfKind) {
                        firstOfKind = false
                    }
                    if (cup.doubleMatch) {
                        newMatches.add(
                            Match(
                                stage = stage,
                                firstOfKind = false,
                                firstTeamJson = Converters().fromTeam(secondTeam),
                                secondTeamJson = Converters().fromTeam(firstTeam),
                                firstTeamRounds = initialRounds,
                                secondTeamRounds = initialRounds,
                                firstMatch = false,
                                idCup = idCup.toInt(),
                            )
                        )
                    }
                } else {
                    var pointsGiven = cup.requiredPoints ?: 10
                    val roundsGiven = cup.requiredRounds
                    if (cup.requiredPoints != null && cup.requiredRounds != null) {
                        pointsGiven = cup.requiredPoints!! * cup.requiredRounds!!
                    }
                    if (firstTeam.id != -1) {
                        newMatches.add(
                            Match(
                                stage = stage,
                                firstOfKind = firstOfKind,
                                firstTeamJson = Converters().fromTeam(firstTeam),
                                secondTeamJson = Converters().fromTeam(secondTeam),
                                firstTeamRounds = roundsGiven,
                                secondTeamRounds = initialRounds,
                                firstTeamPoints = pointsGiven,
                                secondTeamPoints = 0,
                                playable = false,
                                idCup = idCup.toInt(),
                            )
                        )
                        if (firstOfKind) {
                            firstOfKind = false
                        }
                        if (cup.doubleMatch) {
                            newMatches.add(
                                Match(
                                    stage = stage,
                                    firstOfKind = firstOfKind,
                                    firstTeamJson = Converters().fromTeam(secondTeam),
                                    secondTeamJson = Converters().fromTeam(firstTeam),
                                    firstTeamRounds = initialRounds,
                                    secondTeamRounds = roundsGiven,
                                    firstTeamPoints = 0,
                                    secondTeamPoints = pointsGiven,
                                    playable = false,
                                    firstMatch = false,
                                    idCup = idCup.toInt(),
                                )
                            )
                        }
                    } else if (secondTeam.id != -1) {
                        newMatches.add(
                            Match(
                                stage = stage,
                                firstOfKind = firstOfKind,
                                firstTeamJson = Converters().fromTeam(firstTeam),
                                secondTeamJson = Converters().fromTeam(secondTeam),
                                firstTeamRounds = initialRounds,
                                secondTeamRounds = roundsGiven,
                                firstTeamPoints = 0,
                                secondTeamPoints = pointsGiven,
                                playable = false,
                                idCup = idCup.toInt(),
                            )
                        )
                        if (firstOfKind) {
                            firstOfKind = false
                        }
                        if (cup.doubleMatch) {
                            newMatches.add(
                                Match(
                                    stage = stage,
                                    firstOfKind = firstOfKind,
                                    firstTeamJson = Converters().fromTeam(secondTeam),
                                    secondTeamJson = Converters().fromTeam(firstTeam),
                                    firstTeamRounds = roundsGiven,
                                    secondTeamRounds = initialRounds,
                                    firstTeamPoints = pointsGiven,
                                    secondTeamPoints = 0,
                                    playable = false,
                                    firstMatch = false,
                                    idCup = idCup.toInt(),
                                )
                            )
                        }
                    }
                }
                if (firstOfKind) {
                    firstOfKind = false
                }
            }
            dbAccess.matchDao().insertMatches(newMatches)
            updateCup(idCup)
        }
    }


    private fun roundRobin(idCup: String) {
        lifecycleScope.launch {
            val cup = dbAccess.cupDao().getCupById(idCup)
            val listOfTeams =
                dbAccess.teamDao().getTeamsByCupId(idCup).toMutableList()

            if (listOfTeams.size < 2) {
                throw IllegalArgumentException("You need at least two teams")
            }

            val newMatches = mutableListOf<Match>()

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
                        if (cup.requiredRounds == null) {
                            initialRounds = null
                        }
                        newMatches.add(
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
                            newMatches.add(
                                Match(
                                    matchDay = matchDay + matchDaysAmount,
                                    firstOfKind = firstOfKind,
                                    firstTeamJson = secondTeamJson,
                                    secondTeamJson = firstTeamJson,
                                    firstTeamRounds = initialRounds,
                                    secondTeamRounds = initialRounds,
                                    firstMatch = false,
                                    idCup = idCup.toInt()
                                )
                            )
                        }
                        if (firstOfKind) {
                            firstOfKind = false
                        }
                    }
                }
                val mixer = listOfTeams.removeAt(listOfTeams.size - 1)
                listOfTeams.add(1, mixer)
            }
            dbAccess.matchDao().insertMatches(newMatches)
            updateCup(idCup)
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
        activityMainMenu.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

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