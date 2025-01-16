package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.ActivityCupInsight.Companion.ID_CUP_CI
import com.example.scoreit.components.Cup
import com.example.scoreit.components.Match
import com.example.scoreit.components.Team
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.database.Converters
import com.example.scoreit.databinding.ActivityRefereeButtonsBinding
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/*
binding.botonDescansoSegundoEquipo.setOnClickListener {
            iniciarTemporizador()
        }

        binding.startPauseButton.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        binding.resetButton.setOnClickListener {
            resetTimer()
        }

    }

//    private fun iniciarTemporizador() {
//        ocultarVisibilidad()
//        binding.timerDescanso.visibility = View.VISIBLE
//
//        val timer = object : CountDownTimer(5000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                binding.timerDescanso.text = " ${millisUntilFinished / 1000}"
//            }
//
//            override fun onFinish() {
//                mostrarVisibilidad()
//                binding.timerDescanso.visibility = View.GONE
//            }
//        }
//        timer.start()
//    }
//    private fun ocultarVisibilidad(){
//        binding.primerEquipo.visibility = View.GONE
//        binding.botonPrimerEquipo.visibility = View.GONE
//        binding.puntajePrimerEquipo.visibility = View.GONE
//        binding.segundoEquipo.visibility= View.GONE
//        binding.botonSegundoEquipo.visibility = View.GONE
//        binding.puntajeSegundoEquipo.visibility = View.GONE
//        binding.nombrePrimerEquipo.visibility = View.GONE
//        binding.segundoEquipo.visibility = View.GONE
//        binding.timer.visibility = View.GONE
//        binding.startPauseButton.visibility = View.GONE
//        binding.resetButton.visibility = View.GONE
//        binding.botonDescansoPrimerEquipo.visibility = View.GONE
//        binding.botonDescansoSegundoEquipo.visibility = View.GONE
//        binding.botonFinalizarPartido.visibility = View.GONE
//        binding.botonSuspender.visibility = View.GONE
//    }
//
//    private fun mostrarVisibilidad() {
//        binding.primerEquipo.visibility = View.VISIBLE
//        binding.botonPrimerEquipo.visibility = View.VISIBLE
//        binding.puntajePrimerEquipo.visibility = View.VISIBLE
//        binding.segundoEquipo.visibility = View.VISIBLE
//        binding.botonSegundoEquipo.visibility = View.VISIBLE
//        binding.puntajeSegundoEquipo.visibility = View.VISIBLE
//        binding.nombrePrimerEquipo.visibility = View.VISIBLE
//        binding.segundoEquipo.visibility = View.VISIBLE
//        binding.startPauseButton.visibility = View.VISIBLE
//        binding.resetButton.visibility = View.VISIBLE
//        binding.botonDescansoPrimerEquipo.visibility = View.VISIBLE
//        binding.botonDescansoSegundoEquipo.visibility = View.VISIBLE
//        binding.botonFinalizarPartido.visibility = View.VISIBLE
//        binding.botonSuspender.visibility = View.VISIBLE
//    }


      private fun startTimer() {
          // Verificar que haya un valor válido en el EditText
          val input = binding.input.text.toString()
          if (input.isEmpty()) {
              Toast.makeText(this, "Por favor ingresa un tiempo en minutos", Toast.LENGTH_SHORT).show()
              return
          }

          // Convertir el valor ingresado en minutos a milisegundos
          val minutes = input.toLong()
          timeInMillis = minutes * 60 * 1000 // Convertir minutos a milisegundos
          // Iniciar el temporizador
         countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
              override fun onTick(millisUntilFinished: Long) {
                  timeRemaining = millisUntilFinished
                  binding.timer.text = formatTime(millisUntilFinished)
              }

              override fun onFinish() {
                  isRunning = false
                  binding.timer.text = "00:00:00"
                  binding.startPauseButton.icon = getDrawable(R.drawable.play_button)
                  Toast.makeText(this@ActivityRefereeButtons, "¡Se terminó el tiempo!", Toast.LENGTH_SHORT).show()
              }
          }.start()

          isRunning = true
          binding.startPauseButton.icon = getDrawable(R.drawable.pause_button)
      }

      private fun pauseTimer() {
          countDownTimer?.cancel()
          isRunning = false
         binding.startPauseButton.icon = getDrawable(R.drawable.play_button)
     }

      private fun resetTimer() {
        countDownTimer?.cancel()
          isRunning = false
          timeInMillis = 0L
          timeRemaining = 0L
          binding.timer.text = "00:00:00"
          binding.startPauseButton.icon = getDrawable(R.drawable.play_button)
          binding.input.text.clear()
      }

      private fun formatTime(millis: Long): String {
          val hours = millis / (1000 * 60 * 60)
          val minutes = (millis / (1000 * 60)) % 60
          val seconds = (millis / 1000) % 60
          return String.format("%02d:%02d:%02d", hours, minutes, seconds)
      }

    //    private fun descanso(){
//        lifecycleScope.launch {
//            val hayDescansos = dbAccess.partidoDao().obtenerSiPartidoPorPuntos("ID_PARTIDO")
//            binding.botonDescansoPrimerEquipo.setOnClickListener {
//                if (hayDescansos) {
//
//                }
//            }
//        }
//    }
//
//    private fun obtenerTiempoDeJuego() {
//        lifecycleScope.launch {
//            dbAccess.partidoDao()
//                .obtenerTiempoDelPartido(intent.getStringExtra(ID_PARTIDO_PA).toString())
//        }
//    }
}*/

//TODO

class ActivityRefereeButtons : AppCompatActivity() {

    private lateinit var binding: ActivityRefereeButtonsBinding
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_MATCH_RB: String = "ID_MATCH"
    }

    private var totalFirstTeamPoints = 0
    private var totalSecondTeamPoints = 0
    private var firstTeamPoints = 0
    private var secondTeamPoints = 0
    private var firstTeamRounds = 0
    private var secondTeamRounds = 0
    private var requiredDifference = 0
    private var requiredPoints = 1000
    private var requiredRounds = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRefereeButtonsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbAccess = getDatabase(this)

        setButtons()
        setCupData()

        addPointButtons()
        finishButton()
    }

    private fun finishButton() {
        binding.finishButton.setOnClickListener {
            lifecycleScope.launch {
                val idMatch = intent.getStringExtra(ID_MATCH_RB)
                if (idMatch != null) {
                    val match = dbAccess.matchDao().getMatchById(idMatch)
                    val firstTeam = Converters().toTeam(match.firstTeamJson)
                    val secondTeam = Converters().toTeam(match.secondTeamJson)
                    val firstTeamId = firstTeam.id.toString()
                    val secondTeamId = secondTeam.id.toString()
                    val actualFirstTeam = dbAccess.teamDao().getTeamById(firstTeamId)
                    val actualSecondTeam = dbAccess.teamDao().getTeamById(secondTeamId)
                    var winner = 0

                    if (requiredRounds == 1) {
                        totalFirstTeamPoints = firstTeamPoints
                        totalSecondTeamPoints = secondTeamPoints
                        if (firstTeamPoints > secondTeamPoints) {
                            winner = 1
                        } else if (secondTeamPoints > firstTeamPoints) {
                            winner = 2
                        }
                        if (firstTeamPoints - secondTeamPoints >= requiredDifference || secondTeamPoints - firstTeamPoints >= requiredDifference) {
                            update(match, actualFirstTeam, actualSecondTeam, winner)
                        } else {
                            var message = 1
                            if (requiredDifference == 2) message = 2
                            errorMessage(message)
                        }
                    } else {
                        if (firstTeamRounds > secondTeamRounds) {
                            winner = 1
                        } else if (secondTeamRounds > firstTeamRounds) {
                            winner = 2
                        }
                        if (firstTeamRounds == requiredRounds || secondTeamRounds == requiredRounds) {
                            update(match, actualFirstTeam, actualSecondTeam, winner)
                        }
                    }
                }
            }
        }
    }

    private fun setCupData() {
        val idMatch = intent.getStringExtra(ID_MATCH_RB)
        if (idMatch != null) {
            lifecycleScope.launch {
                val idCup = dbAccess.matchDao().getMatchById(idMatch).idCup.toString()
                val cup = dbAccess.cupDao().getCupById(idCup)
                if (cup.twoPointsDifference) {
                    requiredDifference = 2
                } else if (cup.alwaysWinner) {
                    requiredDifference = 1
                }
                requiredPoints = cup.requiredPoints ?: 1000

                if (cup.requiredRounds != null) {
                    requiredRounds = cup.requiredRounds!!
                }

                val match = dbAccess.matchDao().getMatchById(idMatch)
                val firstTeam = Converters().toTeam(match.firstTeamJson)
                val secondTeam = Converters().toTeam(match.secondTeamJson)
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
            firstTeam.pointsWon += totalFirstTeamPoints
            firstTeam.pointsLost += totalSecondTeamPoints

            secondTeam.pointsWon += totalSecondTeamPoints
            secondTeam.pointsLost += totalFirstTeamPoints

            if (firstTeam.roundsWon != null) {
                firstTeam.roundsWon = firstTeam.roundsWon!! + firstTeamRounds
                firstTeam.roundsLost = firstTeam.roundsLost!! + secondTeamRounds
            } else {
                firstTeam.roundsWon = null
                firstTeam.roundsLost = null
            }

            if (secondTeam.roundsWon != null) {
                secondTeam.roundsWon = secondTeam.roundsWon!! + secondTeamRounds
                secondTeam.roundsLost = secondTeam.roundsLost!! + firstTeamRounds
            } else {
                secondTeam.roundsWon = null
                secondTeam.roundsLost = null
            }

            match.playable = false

            match.firstTeamPoints = totalFirstTeamPoints
            match.secondTeamPoints = totalSecondTeamPoints

            if (requiredRounds == 1) {
                match.firstTeamRounds = null
                match.secondTeamRounds = null
            } else {
                match.firstTeamRounds = firstTeamRounds
                match.secondTeamRounds = secondTeamRounds
            }

            when (winner) {
                1 -> {
                    firstTeam.finalPoints += 3
                    firstTeam.matchesWon += 1
                    secondTeam.matchesLost += 1
                }

                2 -> {
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

            val idCup = match.idCup.toString()
            val cup = dbAccess.cupDao().getCupById(idCup)
            if (!cup.hasStarted) {
                cup.hasStarted = true
                dbAccess.cupDao().update(cup)
            }

            checkForNextStage(cup, match)
            changeToActivityCupInsight(idCup)
        }
    }

    private fun checkForNextStage(cup: Cup, match: Match) {
        lifecycleScope.launch {
            if (cup.gameMode != "Round Robin") {
                val listOfMatches = dbAccess.matchDao().getMatchesByCupId(cup.id.toString())
                val playableMatches = listOfMatches.filter { !it.playable }.size
                if (cup.playableMatches == playableMatches) {
                    if (cup.gameMode == "Brackets") {

                    } else if (cup.gameMode == "Both") {
                        val listOfTeams = dbAccess.teamDao().getTeamsByCupId(cup.id.toString())
                        val sortedListOfTeams = listOfTeams.sortedWith(
                            compareByDescending<Team> { it.finalPoints }
                                .thenByDescending { (it.pointsWon - it.pointsLost) }
                                .thenBy { it.name }
                        )
                        createNewMatches(cup, match, sortedListOfTeams)
                    }
                }
            }
        }
    }

    private fun createNewMatches(cup: Cup, match: Match, listOfTeams: List<Team>) {
        lifecycleScope.launch {
            var firstOfKind = true
            val initialRounds = cup.requiredRounds ?: 0

            val newMatches = mutableListOf<Match>()

            val stage: Int = if (match.stage != null) {
                match.stage - 1
            } else {
                1.shl(
                    (listOfTeams.size - 1).takeHighestOneBit().countTrailingZeroBits() + 1
                ) / 2
            }

            for (i in 0 until listOfTeams.size / 2) {
                val firstTeam = listOfTeams[i]
                val secondTeam = listOfTeams[listOfTeams.size - 1 - i]
                newMatches.add(
                    Match(
                        stage = stage,
                        firstOfKind = firstOfKind,
                        firstTeamJson = Converters().fromTeam(firstTeam),
                        secondTeamJson = Converters().fromTeam(secondTeam),
                        firstTeamRounds = initialRounds,
                        secondTeamRounds = initialRounds,
                        idCup = cup.id
                    )
                )
                if (cup.doubleMatch) {
                    newMatches.add(
                        Match(
                            stage = stage,
                            firstOfKind = false,
                            firstTeamJson = Converters().fromTeam(secondTeam),
                            secondTeamJson = Converters().fromTeam(firstTeam),
                            firstTeamRounds = initialRounds,
                            secondTeamRounds = initialRounds,
                            idCup = cup.id,
                        )
                    )
                }
                if (firstOfKind) {
                    firstOfKind = false
                }
            }
            dbAccess.matchDao().insertMatches(newMatches)
        }
    }

    private fun addPointButtons() {
        binding.firstTeamFrame.setOnClickListener {
            addPoint(true)
        }
        binding.secondTeamFrame.setOnClickListener {
            addPoint(false)
        }
    }

    private fun addPoint(isFirstTeam: Boolean) {
        lifecycleScope.launch {
            val idMatch = intent.getStringExtra(ID_MATCH_RB)
            if (idMatch != null) {
                if (isFirstTeam) {
                    if (requiredRounds == 1) {
                        if ((firstTeamPoints < requiredPoints && secondTeamPoints < requiredPoints) || (firstTeamPoints - secondTeamPoints).absoluteValue < requiredDifference) {
                            firstTeamPoints++
                            val newPoints = firstTeamPoints.toString()
                            binding.firstTeamPoints.text = newPoints
                        } else {
                            totalFirstTeamPoints = firstTeamPoints
                            totalSecondTeamPoints = secondTeamPoints
                        }
                    } else {
                        if (firstTeamRounds < requiredRounds && secondTeamRounds < requiredRounds) {
                            firstTeamPoints++
                            if (firstTeamPoints > requiredPoints &&
                                (firstTeamPoints - (secondTeamPoints + 1) > requiredDifference)
                            ) {
                                firstTeamPoints--
                                firstTeamRounds++
                                resetPoints()
                                if (firstTeamRounds <= requiredRounds) {
                                    val newRounds = "($firstTeamRounds)"
                                    binding.firstTeamRounds.text = newRounds
                                }
                            } else {
                                if ((firstTeamPoints < requiredPoints && secondTeamPoints < requiredPoints) || firstTeamPoints - secondTeamPoints < requiredDifference) {
                                    val newPoints = firstTeamPoints.toString()
                                    binding.firstTeamPoints.text = newPoints
                                }
                            }
                        }
                    }
                } else {
                    if (requiredRounds == 1) {
                        if ((secondTeamPoints < requiredPoints && firstTeamPoints < requiredPoints) || (firstTeamPoints - secondTeamPoints).absoluteValue < requiredDifference) {
                            secondTeamPoints++
                            val newPoints = secondTeamPoints.toString()
                            binding.secondTeamPoints.text = newPoints
                        }
                    } else {
                        if (secondTeamRounds < requiredRounds && firstTeamRounds < requiredRounds) {
                            secondTeamPoints++
                            if (secondTeamPoints > requiredPoints &&
                                (secondTeamPoints - (firstTeamPoints + 1) > requiredDifference)
                            ) {
                                secondTeamPoints--
                                secondTeamRounds++
                                resetPoints()
                                if (secondTeamRounds <= requiredRounds) {
                                    val newRounds = "($secondTeamRounds)"
                                    binding.secondTeamRounds.text = newRounds
                                }
                            } else {
                                if ((firstTeamPoints < requiredPoints && secondTeamPoints < requiredPoints) || secondTeamPoints - firstTeamPoints < requiredDifference) {
                                    val newPoints = secondTeamPoints.toString()
                                    binding.secondTeamPoints.text = newPoints
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun resetPoints() {
        val idMatch = intent.getStringExtra(ID_MATCH_RB)
        if (idMatch != null) {
            lifecycleScope.launch {
                //val match = dbAccess.matchDao().getMatchById(idMatch)
                //match.pointsPerRoundFirstTeam.add(firstTeamPoints.toString())
                //match.pointsPerRoundSecondTeam.add(secondTeamPoints.toString())
                //dbAccess.matchDao().update(match)

                totalFirstTeamPoints += firstTeamPoints
                totalSecondTeamPoints += secondTeamPoints
                firstTeamPoints = 0
                secondTeamPoints = 0
                binding.firstTeamPoints.text = "0"
                binding.secondTeamPoints.text = "0"
            }
        }
    }

    private fun errorMessage(message: Int) {
        if (message == 1) {
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
        activityCupInsight.putExtra(ID_CUP_CI, idCup)

        startActivity(activityCupInsight)
        finish()
    }
}