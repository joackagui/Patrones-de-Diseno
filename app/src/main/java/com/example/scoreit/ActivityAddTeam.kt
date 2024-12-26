package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreit.ActivityMainMenu.Companion.ID_USER_MM
import com.example.scoreit.adapters.RecyclerTeams
import com.example.scoreit.componentes.Team
import com.example.scoreit.componentes.Match
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.database.Converters
import com.example.scoreit.databinding.ActivityAddTeamBinding
import kotlinx.coroutines.launch

class ActivityAddTeam : AppCompatActivity() {

    private val recyclerTeams: RecyclerTeams by lazy { RecyclerTeams() }
    private lateinit var binding: ActivityAddTeamBinding
    private lateinit var dbAccess: AppDataBase

    private var num = 3

    companion object {
        const val ID_USER_AT: String = "ID_USER"
        const val ID_CUP_AT: String = "ID_CUP"
    }

    private val idUser = intent.getStringExtra(ID_USER_AT)
    private val idCup = intent.getStringExtra(ID_CUP_AT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbAccess = getDatabase(this)

        setHeader()
        defaultTeams()

        addButton()

        setUpRecyclerView()
        saveButton()
    }

    private fun AppCompatActivity.setHeader() {
//        val userButton = findViewById<Button>(R.id.user_button)
//        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)
//
//        userButton.setOnClickListener {
//            resetCup()
//            val intent = Intent(this, ActivityLogIn::class.java)
//            startActivity(intent)
//        }
//
//        scoreItCup.setOnClickListener {
//            resetCup()
//            val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
//            activityMainMenu.putExtra(ID_USER_MM, intent.getStringExtra(idUser))
//            startActivity(activityMainMenu)
//        }
    }

    private fun resetCampeonato() {
//        lifecycleScope.launch {
//            val campeonatoid = intent.getStringExtra(ID_CAMPEONATO_DE)
//            if(campeonatoid != null){
//                val campeonatoPorResetear = dbAccess.campeonatoDao().obtenerPorId(campeonatoid)
//                campeonatoPorResetear.nombreCampeonato = "Cup $campeonatoid"
//                campeonatoPorResetear.seleccionado = false
//                campeonatoPorResetear.fechaDeInicio = "--:--:--"
//                campeonatoPorResetear.seJuegaPorPuntosMaximos = false
//                campeonatoPorResetear.puntosParaGanar = 100
//                campeonatoPorResetear.seJuegaPorTiempoMaximo = false
//                campeonatoPorResetear.tiempoDeJuego = 45
//                campeonatoPorResetear.modoDeJuego = "Round Robin"
//                campeonatoPorResetear.permisoDeDescanso = false
//                campeonatoPorResetear.tiempoDeDescanso = 1
//                campeonatoPorResetear.cantidadDeDescansos = 2
//                campeonatoPorResetear.permisoDeRonda = false
//                campeonatoPorResetear.cantidadDeRondas = 2
//                campeonatoPorResetear.idaYVuelta = false
//                campeonatoPorResetear.siempreUnGanador = true
//                campeonatoPorResetear.diferenciaDosPuntos = false
//
//                dbAccess.campeonatoDao().update(campeonatoPorResetear)
//            }
//        }
    }

    private fun defaultTeams() {
        if (idCup != null) {
            lifecycleScope.launch {
                val firstTwoTeams: MutableList<Team> = mutableListOf()
                val firstTeam = Team(name = "Team 1", idCup = idCup.toInt())
                val secondTeam = Team(name = "Team 2", idCup = idCup.toInt())
                dbAccess.teamDao().insert(firstTeam)
                dbAccess.teamDao().insert(secondTeam)
                firstTwoTeams.add(firstTeam)
                firstTwoTeams.add(secondTeam)
                recyclerTeams.addDataToList(firstTwoTeams)
                setUpRecyclerView()
            }
        }
    }

    private fun addButton() {
        binding.addTeamButton.setOnClickListener {
            addNewTeam()
        }
    }

    private fun saveButton() {
        binding.saveButton.setOnClickListener {
            if (idCup != null) {
                createMatches()
                changeToActivityMainMenu()
            }

        }
    }

    private fun createMatches() {
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
                    roundRobin()
                    brackets()
                }
            }
        }
    }

    private fun brackets() {
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
//                    2 to "Final",
//                    4 to "Semi-final",
//                    8 to "Quarter-final",
//                    16 to "Round of 16",
//                    32 to "Round of 32"
//                )
//
//                var rondaEquipos = listOfTeams
//                var rondaActual = nextTwoExponential
//                var jornada = 1
//
//                val matches = mutableListOf<Match>()
//
//                while (rondaActual > 1) {
//                    val nombreFase = stages[rondaActual] ?: "Fase $jornada"
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
//                                jornada = nombreFase,
//                                primerEquipoJson = localGson,
//                                segundoEquipoJson = visitanteGson,
//                                puntosPrimerEquipo = 0,
//                                puntosSegundoEquipo = 0,
//                                rondasPrimerEquipo = "",
//                                rondasSegundoEquipo = "",
//                                porRondas = false,
//                                idCampeonato = campeonatoid
//                            )
//                        )
//
//                        nuevaRonda.add(if (local.id != -1) local else visitante)
//                    }
//
//                    rondaEquipos = nuevaRonda
//                    rondaActual /= 2
//                    jornada++
//                }
//
//                dbAccess.partidoDao().insertarPartidos(matches)
//
//            }
//        }
    }

    private fun roundRobin() {
        if(idCup != null){
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
                    for (i in 0 until teamsHalf) {
                        val firstTeam = listOfTeams[i]
                        val secondTeam = listOfTeams[listOfTeams.size - 1 - i]

                        if (firstTeam.id != -1 && secondTeam.id != -1) {
                            val firstTeamJson = Converters().fromTeam(firstTeam)
                            val secondTeamJson = Converters().fromTeam(secondTeam)
                            val stage = "Match day $matchDay"

                            matches.add(
                                Match(
                                    stage = stage,
                                    firstTeamJson = firstTeamJson,
                                    secondTeamJson = secondTeamJson,
                                    idCup = idCup.toInt()
                                )
                            )
                            if (cup.twoMatches) {
                                val newStage = "Match day ${matchDaysAmount + 1 - matchDay}"
                                matches.add(
                                    Match(
                                        stage = newStage,
                                        firstTeamJson = secondTeamJson,
                                        secondTeamJson = firstTeamJson,
                                        idCup = idCup.toInt()
                                    )
                                )
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
        if(idCup != null){
            lifecycleScope.launch {
                val listOfTeams = dbAccess.teamDao().getTeamsByCupId(idCup).toMutableList()
                recyclerTeams.addDataToList(listOfTeams)
            }
            binding.recyclerTeams.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = recyclerTeams
            }
        }
    }

    private fun addNewTeam() {
        if (idCup != null) {
            lifecycleScope.launch {
                val newTeam = Team(name = "Team $num", idCup = idCup.toInt())
                num += 1
                dbAccess.teamDao().insert(newTeam)
                setUpRecyclerView()
            }
        }
    }

    private fun changeToActivityMainMenu() {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ID_USER_MM, idUser)
        startActivity(activityMainMenu)
    }
}
