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

    // Adaptador para la lista de partidos
    private lateinit var recyclerMatches: RecyclerMatches

    // Inicialización perezosa del adaptador para la tabla de posiciones
    private val recyclerScoreBoardRows: RecyclerScoreBoardRows by lazy { RecyclerScoreBoardRows() }

    // Binding para la actividad de visualización de la copa
    private lateinit var binding: ActivityCupInsightBinding

    // Acceso a la base de datos
    private lateinit var dbAccess: AppDataBase

    // Objeto companion para definir constantes
    companion object {
        const val ID_CUP_CI: String = "ID_CUP" // Clave para pasar el ID de la copa entre actividades
    }

    // Se llama a onCreate cuando la actividad es creada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el layout usando view binding
        binding = ActivityCupInsightBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Inicializar el acceso a la base de datos
        dbAccess = getDatabase(this)

        // Inicializar el adaptador de partidos
        recyclerMatches = RecyclerMatches(dbAccess, lifecycleScope)

        // Configurar la cabecera con los botones de usuario y copa
        setHeader()

        // Establecer los datos de la copa en la UI
        setCupData()

        // Configurar el RecyclerView para la tabla de posiciones
        setUpRecyclerScoreBoard()

        // Configurar el RecyclerView para la lista de partidos
        setUpRecyclerViewMatches()

        // Configurar el botón de retroceso
        backButton()

        // Configurar el botón de edición
        editButton()
    }

    // Función para configurar la cabecera con los botones de usuario y copa
    private fun AppCompatActivity.setHeader() {
        val userButton = findViewById<ImageView>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)

        // Configurar el listener del botón de usuario
        userButton.setOnClickListener {
            lifecycleScope.launch {
                val idCup = intent.getStringExtra(ID_CUP_CI)
                if (idCup != null) {
                    val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    changeToActivityChangeUserData(idUser)
                }
            }
        }

        // Configurar el listener del botón de copa
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

    // Función para configurar el botón de edición
    private fun editButton() {
        binding.editButton.setOnClickListener {
            val idCup = intent.getStringExtra(ID_CUP_CI)
            if (idCup != null) {
                lifecycleScope.launch {
                    val cup = dbAccess.cupDao().getCupById(idCup)
                    if (cup.winner != null) {
                        // Si la copa ya tiene un ganador, eliminar la copa
                        deleteCup(idCup)
                    } else {
                        // Si no tiene ganador, navegar a la actividad de configuración de nueva copa
                        changeToActivityNewCupSettings(idCup)
                    }
                }
            }
        }
    }

    // Función para eliminar la copa y sus datos asociados
    private fun deleteCup(idCup: String) {
        lifecycleScope.launch {
            // Eliminar partidos, equipos y la copa de la base de datos
            dbAccess.matchDao().deleteMatchesByIdCup(idCup)
            dbAccess.teamDao().deleteTeamsByIdCup(idCup)
            dbAccess.cupDao().deleteById(idCup)

            // Navegar al menú principal
            val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
            changeToActivityMainMenu(idUser)
        }
    }

    // Función para configurar el botón de retroceso
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

    // Función para establecer los datos de la copa en la UI
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

                // Establecer los valores en los campos de texto de la UI
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
                    // Si hay un ganador, cambiar el texto del botón de edición a "Eliminar"
                    val deleteText = "Eliminar"
                    binding.editButton.text = deleteText

                    // Mostrar el nombre del ganador en la UI
                    binding.winnerTag.visibility = View.VISIBLE
                    binding.winnerName.visibility = View.VISIBLE
                    binding.winnerName.text = winnerName

                    // Mostrar un mensaje de que la copa ha finalizado
                    finishMessage()
                }
            }
        }
    }

    // Función para configurar el RecyclerView de partidos
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

    // Función para configurar el RecyclerView de la tabla de posiciones
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

    // Función para mostrar un mensaje de que la copa ha finalizado
    private fun finishMessage() {
        val idCup = intent.getStringExtra(ID_CUP_CI)
        if (idCup != null) {
            lifecycleScope.launch {
                val cup = dbAccess.cupDao().getCupById(idCup)
                val finishMessage = "La copa ${cup.name} ha finalizado"
                Toast.makeText(this@ActivityCupInsight, finishMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Función para navegar al menú principal
    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ActivityMainMenu.Companion.ID_USER_MM, idUser)
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish()
    }

    // Función para navegar a la actividad de cambio de datos de usuario
    private fun changeToActivityChangeUserData(idUser: String) {
        val activityChangeUserData = Intent(this, ActivityChangeUserData::class.java)
        activityChangeUserData.putExtra(ActivityChangeUserData.Companion.ID_USER_CUD, idUser)

        startActivity(activityChangeUserData)
    }

    // Función para navegar a la actividad de configuración de nueva copa
    private fun changeToActivityNewCupSettings(idCup: String) {
        val activityNewCupSettings = Intent(this, ActivityNewCupSettings::class.java)
        activityNewCupSettings.putExtra(ActivityNewCupSettings.Companion.ID_CUP_NC, idCup)

        startActivity(activityNewCupSettings)
    }
}