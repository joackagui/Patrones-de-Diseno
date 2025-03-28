package com.example.scoreit.controller.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreit.R
import com.example.scoreit.controller.activities.ActivityNewTeamSettings.Companion.ID_CUP_NT
import com.example.scoreit.view.recyclers.RecyclerTeams
import com.example.scoreit.model.components.Match
import com.example.scoreit.model.components.Team
import com.example.scoreit.model.database.AppDataBase
import com.example.scoreit.model.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.model.database.Converters
import com.example.scoreit.databinding.ActivityAddTeamBinding
import kotlinx.coroutines.launch

class ActivityAddTeam : AppCompatActivity() {

    // Inicialización perezosa del adaptador RecyclerTeams
    private val recyclerTeams: RecyclerTeams by lazy { RecyclerTeams() }

    // Binding para la actividad de agregar equipo
    private lateinit var binding: ActivityAddTeamBinding

    // Acceso a la base de datos
    private lateinit var dbAccess: AppDataBase

    // Objeto companion para definir constantes
    companion object {
        const val ID_CUP_AT: String = "ID_CUP" // Clave para pasar el ID de la copa entre actividades
    }

    // Se llama a onCreate cuando la actividad es creada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el layout usando view binding
        binding = ActivityAddTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el acceso a la base de datos
        dbAccess = getDatabase(this)

        // Configurar la cabecera con los botones de usuario y copa
        setHeader()

        // Configurar equipos por defecto si no existen
        defaultTeams()

        // Configurar el botón de retroceso
        backButton()

        // Configurar el botón de agregar equipo
        addButton()

        // Configurar el botón de guardar
        saveButton()
    }

    // Función para configurar la cabecera con los botones de usuario y copa
    private fun AppCompatActivity.setHeader() {
        val userButton = findViewById<ImageView>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)

        // Configurar el listener del botón de usuario
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

        // Configurar el listener del botón de copa
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

    // Función para configurar el botón de retroceso
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

    // Función para configurar el botón de agregar equipo
    private fun addButton() {
        binding.addTeamButton.setOnClickListener {
            val idCup = intent.getStringExtra(ID_CUP_AT)
            if (idCup != null) {
                changeToActivityNewTeamSettings(idCup)
            }
        }
    }

    // Función para configurar el botón de guardar
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

    // Función para establecer la cantidad de equipos en la UI
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

    // Función para eliminar la copa y sus datos asociados
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

    // Función para configurar equipos por defecto si no existen
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

    // Función para mostrar un mensaje de éxito o error
    private fun successfulMessage(success: Boolean) {
        if (success) {
            Toast.makeText(this, "Creation successful", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "At least two teams are required", Toast.LENGTH_LONG).show()
        }
    }

    // Función para crear partidos para la copa
    private fun createMatches() {
        val idCup = intent.getStringExtra(ID_CUP_AT)
        if (idCup != null) {
            lifecycleScope.launch {
                roundRobin(idCup)
            }
        }
    }

    // Función para actualizar la copa con el número de partidos jugables
    private fun updateCup(idCup: String) {
        lifecycleScope.launch {
            val cup = dbAccess.cupDao().getCupById(idCup)
            val matches = dbAccess.matchDao().getMatchesByCupId(idCup)
            cup.playableMatches = matches.size
            dbAccess.cupDao().update(cup)
        }
    }

    // Función para generar partidos usando el algoritmo round-robin
    private fun roundRobin(idCup: String) {
        lifecycleScope.launch {
            val cup = dbAccess.cupDao().getCupById(idCup)
            val listOfTeams =
                dbAccess.teamDao().getTeamsByCupId(idCup).toMutableList()

            if (listOfTeams.size < 2) {
                throw IllegalArgumentException("Necesitas al menos dos equipos")
            }

            val newMatches = mutableListOf<Match>()

            val isUneven = listOfTeams.size % 2 != 0
            if (isUneven) {
                listOfTeams.add(Team(id = -1, name = "Libre", idCup = idCup.toInt()))
            }

            val matchDaysAmount = listOfTeams.size - 1
            val teamsHalf = listOfTeams.size / 2

            for (matchDay in 1..matchDaysAmount) {
                var firstOfKind = true
                for (i in 0 until teamsHalf) {
                    val firstTeam = listOfTeams[i]
                    val secondTeam = listOfTeams[listOfTeams.size - 1 - i]

                    if (firstTeam.id != -1 && secondTeam.id != -1) {
                        val idFirstTeam = firstTeam.id
                        val idSecondTeam = secondTeam.id

                        var initialRounds: Int? = 0
                        if (cup.requiredRounds == null) {
                            initialRounds = null
                        }
                        newMatches.add(
                            Match(
                                matchDay = matchDay,
                                firstOfKind = firstOfKind,
                                idFirstTeam = idFirstTeam,
                                idSecondTeam = idSecondTeam,
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
                                    idFirstTeam = idSecondTeam,
                                    idSecondTeam = idSecondTeam,
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

    // Función para configurar el RecyclerView con la lista de equipos
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

    // Función para navegar al menú principal
    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ActivityMainMenu.ID_USER_MM, idUser)
        activityMainMenu.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish()
    }

    // Función para navegar a la actividad de configuración de nuevo equipo
    private fun changeToActivityNewTeamSettings(idCup: String) {
        val activityNewTeamSettings = Intent(this, ActivityNewTeamSettings::class.java)
        activityNewTeamSettings.putExtra(ID_CUP_NT, idCup)

        startActivity(activityNewTeamSettings)
    }

    // Función para navegar a la actividad de configuración de nueva copa
    private fun changeToActivityNewCupSettings(lastCupJson: String) {
        val activityNewCupSettings = Intent(this, ActivityNewCupSettings::class.java)
        activityNewCupSettings.putExtra(ActivityNewCupSettings.CUP_JSON_NC, lastCupJson)

        startActivity(activityNewCupSettings)
        finish()
    }

    // Función para navegar a la actividad de cambio de datos de usuario
    private fun changeToActivityChangeUserData(idUser: String) {
        val activityChangeUserData = Intent(this, ActivityChangeUserData::class.java)
        activityChangeUserData.putExtra(ActivityChangeUserData.ID_USER_CUD, idUser)

        startActivity(activityChangeUserData)
    }
}