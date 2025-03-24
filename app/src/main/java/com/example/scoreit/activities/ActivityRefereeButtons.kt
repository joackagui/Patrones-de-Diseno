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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityRefereeButtons : AppCompatActivity() {

    // Binding para la actividad de botones del árbitro
    private lateinit var binding: ActivityRefereeButtonsBinding

    // Acceso a la base de datos
    private lateinit var dbAccess: AppDataBase

    // Controlador para manejar los comandos de puntos
    private lateinit var controller: Controller

    // Gestor de puntos para manejar la lógica de los puntos
    private lateinit var pointsManager: PointsManager

    // Comandos para agregar puntos a los equipos y finalizar el partido
    private lateinit var firstTeamAddPointCommand: FirstTeamAddPointCommand
    private lateinit var secondTeamAddPointCommand: SecondTeamAddPointCommand
    private lateinit var finishCommand: FinishCommand

    // Objeto companion para definir constantes
    companion object {
        const val ID_MATCH_RB: String = "ID_MATCH" // Clave para pasar el ID del partido entre actividades
    }

    // Se llama a onCreate cuando la actividad es creada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el layout usando view binding
        binding = ActivityRefereeButtonsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el acceso a la base de datos
        dbAccess = getDatabase(this)

        // Configurar los comandos de puntos
        setCommands()

        // Configurar los botones en la UI
        setButtons()

        // Establecer los datos de la copa en la UI
        setCupData()

        // Configurar los botones para agregar puntos
        addPointButtons()

        // Configurar el botón de deshacer
        undoButtons()

        // Configurar el botón de finalizar
        finishButton()
    }

    // Función para configurar los comandos de puntos
    private fun setCommands() {
        lifecycleScope.launch {
            val idMatch = intent.getStringExtra(ID_MATCH_RB) ?: return@launch
            val match = dbAccess.matchDao().getMatchById(idMatch)
            val cup = dbAccess.cupDao().getCupById(match.idCup.toString())

            // Inicializar el controlador y el gestor de puntos
            controller = Controller()
            pointsManager = PointsManager(match, cup)

            // Inicializar los comandos para agregar puntos y finalizar el partido
            firstTeamAddPointCommand = FirstTeamAddPointCommand(binding, pointsManager)
            secondTeamAddPointCommand = SecondTeamAddPointCommand(binding, pointsManager)
            finishCommand = FinishCommand(binding, pointsManager)
        }
    }

    // Función para configurar el botón de finalizar
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

                    // Establecer el comando de finalización y ejecutarlo
                    controller.setCommand(finishCommand)
                    var message: Int = controller.execute()

                    // Manejar el resultado de la ejecución del comando
                    if (message < 10) {
                        if (message == -1) {
                            errorMessage(0) // Mostrar mensaje de error si no se puede finalizar
                        } else {
                            // Actualizar los datos del partido y los equipos
                            update(match, firstTeam, secondTeam, message) {
                                changeToActivityCupInsight(cup.id.toString()) // Navegar a la actividad de visualización de la copa
                            }
                        }
                    } else {
                        if (message == 10) {
                            errorMessage(1) // Mostrar mensaje de error si no hay un ganador
                        } else if (message == 11) {
                            errorMessage(2) // Mostrar mensaje de error si no hay una diferencia de dos puntos
                        }
                    }
                }
            }
        }
    }

    // Función para configurar los botones de agregar puntos
    private fun addPointButtons() {
        binding.firstTeamFrame.setOnClickListener {
            controller.setCommand(firstTeamAddPointCommand) // Establecer el comando para agregar puntos al primer equipo
            controller.execute() // Ejecutar el comando
        }
        binding.secondTeamFrame.setOnClickListener {
            controller.setCommand(secondTeamAddPointCommand) // Establecer el comando para agregar puntos al segundo equipo
            controller.execute() // Ejecutar el comando
        }
    }

    // Función para configurar el botón de deshacer
    private fun undoButtons() {
        binding.undoButton.setOnClickListener {
            controller.undo() // Deshacer la última acción
        }
    }

    // Función para establecer los datos de la copa en la UI
    private fun setCupData() {
        val idMatch = intent.getStringExtra(ID_MATCH_RB)
        if (idMatch != null) {
            lifecycleScope.launch {
                val match = dbAccess.matchDao().getMatchById(idMatch)
                val idFirstTeam = match.idFirstTeam
                val idSecondTeam = match.idSecondTeam
                val firstTeam = dbAccess.teamDao().getTeamById(idFirstTeam.toString())
                val secondTeam = dbAccess.teamDao().getTeamById(idSecondTeam.toString())

                // Mostrar los nombres de los equipos en la UI
                binding.firstTeamNameDisplay.text = firstTeam.name
                binding.secondTeamNameDisplay.text = secondTeam.name
            }
        }
    }

    // Función para configurar los botones en la UI
    private fun setButtons() {
        val idMatch = intent.getStringExtra(ID_MATCH_RB)
        if (idMatch != null) {
            lifecycleScope.launch {
                val match = dbAccess.matchDao().getMatchById(idMatch)
                if (match.firstTeamRounds != null && match.secondTeamRounds != null) {
                    // Mostrar las rondas si están disponibles
                    binding.firstTeamRounds.visibility = View.VISIBLE
                    binding.secondTeamRounds.visibility = View.VISIBLE
                    val newFirstTeamRounds = "(${match.firstTeamRounds})"
                    val newSecondTeamRounds = "(${match.secondTeamRounds})"
                    binding.firstTeamRounds.text = newFirstTeamRounds
                    binding.secondTeamRounds.text = newSecondTeamRounds
                } else {
                    // Ocultar las rondas si no están disponibles
                    binding.firstTeamRounds.visibility = View.INVISIBLE
                    binding.secondTeamRounds.visibility = View.INVISIBLE
                }
            }
        }
    }

    // Función para actualizar los datos del partido y los equipos
    private fun update(match: Match, firstTeam: Team, secondTeam: Team, winner: Int, onUpdateComplete: () -> Unit) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val idCup = match.idCup.toString()
                val cup = dbAccess.cupDao().getCupById(idCup)

                // Actualizar los puntos y rondas de los equipos
                firstTeam.pointsWon += pointsManager.totalFirstTeamPoints
                firstTeam.pointsLost += pointsManager.totalSecondTeamPoints

                secondTeam.pointsWon += pointsManager.totalSecondTeamPoints
                secondTeam.pointsLost += pointsManager.totalFirstTeamPoints

                if (firstTeam.roundsWon != null) {
                    firstTeam.roundsWon = firstTeam.roundsWon!! + pointsManager.firstTeamRounds
                    firstTeam.roundsLost = firstTeam.roundsLost!! + pointsManager.secondTeamRounds
                }

                if (secondTeam.roundsWon != null) {
                    secondTeam.roundsWon = secondTeam.roundsWon!! + pointsManager.secondTeamRounds
                    secondTeam.roundsLost = secondTeam.roundsLost!! + pointsManager.firstTeamRounds
                }

                // Marcar el partido como no jugable
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

                // Determinar el ganador y actualizar los puntos finales
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

                // Actualizar los datos en la base de datos
                dbAccess.matchDao().update(match)
                dbAccess.teamDao().update(firstTeam)
                dbAccess.teamDao().update(secondTeam)

                if (!cup.hasStarted) {
                    cup.hasStarted = true
                    dbAccess.cupDao().update(cup)
                }

                // Verificar si la copa ha terminado
                val listOfMatches = dbAccess.matchDao().getMatchesByCupId(cup.id.toString())
                val playedMatches = listOfMatches.filter { !it.playable }.size
                val listOfTeams = dbAccess.teamDao().getTeamsByCupId(cup.id.toString())
                if (cup.playableMatches == playedMatches) {
                    cup.winner = listOfTeams.sortedWith(
                        compareByDescending<Team> { it.finalPoints }
                            .thenByDescending { it.pointsWon - it.pointsLost }
                            .thenBy { it.name }
                    )[0].name
                    dbAccess.cupDao().update(cup)
                }
            }
            onUpdateComplete()
        }
    }

    // Función para mostrar mensajes de error
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
                "There must be a difference of two points",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Función para navegar a la actividad de visualización de la copa
    private fun changeToActivityCupInsight(idCup: String) {
        val activityCupInsight = Intent(this, ActivityCupInsight::class.java)
        activityCupInsight.putExtra(ActivityCupInsight.Companion.ID_CUP_CI, idCup)

        startActivity(activityCupInsight)
        finish()
    }
}