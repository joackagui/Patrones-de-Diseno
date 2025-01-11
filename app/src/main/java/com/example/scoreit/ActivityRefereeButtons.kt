package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.ActivityCupInsight.Companion.ID_CUP_CI
import com.example.scoreit.components.Match
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.database.Converters
import com.example.scoreit.databinding.ActivityRefereeButtonsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

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

    // funciones
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
                  binding.startPauseButton.icon = getDrawable(R.drawable.playbutton)
                  Toast.makeText(this@ActivityRefereeButtons, "¡Se terminó el tiempo!", Toast.LENGTH_SHORT).show()
              }
          }.start()

          isRunning = true
          binding.startPauseButton.icon = getDrawable(R.drawable.pausebutton)
      }

      private fun pauseTimer() {
          countDownTimer?.cancel()
          isRunning = false
         binding.startPauseButton.icon = getDrawable(R.drawable.playbutton)
     }

      private fun resetTimer() {
        countDownTimer?.cancel()
          isRunning = false
          timeInMillis = 0L
          timeRemaining = 0L
          binding.timer.text = "00:00:00"
          binding.startPauseButton.icon = getDrawable(R.drawable.playbutton)
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
//


    private fun addPoint(isFirstTeam: Boolean) {
        val idMatch = intent.getStringExtra(ID_MATCH_RB)
        if (idMatch != null) {
            lifecycleScope.launch {
                val match_day_sign = dbAccess.matchDao().getMatchById(idMatch)
                val idCup = match_day_sign.idCup.toString()
                val cup = dbAccess.cupDao().getCupById(idCup)
                val winningPoints =
                    cup.winningPoints ?: Int.MAX_VALUE
                val roundsLimit = cup.roundsAmount

                val currentScore = if (isFirstTeam) {
                    binding.firstTeamScore.text.toString().toInt()
                } else {
                    binding.secondTeamScore.text.toString().toInt()
                }

                val currentRounds = if (isFirstTeam) {
                    match_day_sign.firstTeamRounds?.toInt() ?: 0
                } else {
                    match_day_sign.secondTeamRounds?.toInt() ?: 0
                }

                if (currentScore >= winningPoints) {
                    if (roundsLimit != null && currentRounds + 1 < roundsLimit) {
                        val newRound = (currentRounds + 1).toString()
                        if (isFirstTeam) {
                            match_day_sign.firstTeamRounds = newRound
                            binding.firstTeamRounds.text = newRound
                        } else {
                            match_day_sign.secondTeamRounds = newRound
                            binding.secondTeamRounds.text = newRound
                        }
                        match_day_sign.firstTeamPoints += binding.firstTeamScore.text.toString().toInt()
                        match_day_sign.secondTeamPoints += binding.secondTeamScore.text.toString().toInt()

                        binding.firstTeamScore.text = "0"
                        binding.secondTeamScore.text = "0"

                        dbAccess.matchDao().update(match_day_sign)
                        // TODO: Guardar los cambios en la base de datos
                    } else {
                        //matchEnded(match_day_sign)
                    }
                } else {
                    val newScore = (currentScore + 1).toString()
                    if (isFirstTeam) {
                        binding.firstTeamScore.text = newScore
                    } else {
                        binding.secondTeamScore.text = newScore
                    }
                }
            }
        }
    }
}*/

class ActivityRefereeButtons : AppCompatActivity() {

    private lateinit var binding: ActivityRefereeButtonsBinding
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_MATCH_RB: String = "ID_MATCH"
    }

    private var firstTeamPoints = 0
    private var secondTeamPoints = 0
    private var requiredDifference = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRefereeButtonsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización de la base de datos
        dbAccess = getDatabase(this)

        // Configurar botones y listeners
        setListeners()
        setButtons()
        finishButton()
    }

    private fun assignPoints(match: Match) {
        lifecycleScope.launch {
            val firstTeam = Converters().toTeam(match.firstTeamJson)
            val secondTeam = Converters().toTeam(match.secondTeamJson)


        }
    }


private fun setListeners() {
    // Escucha de eventos para los botones de sumar y restar puntos
    binding.firstTeamFrame.setOnClickListener { addPoint(true) }
    binding.secondTeamFrame.setOnClickListener { addPoint(false) }

    binding.finishButton.setOnClickListener { finishButton() }
}

private fun setButtons() {
    // Configurar comportamiento de botones adicionales
    binding.startPauseButton.setOnClickListener {
        Snackbar.make(
            binding.root,
            "Función Start/Pause pendiente de implementación.",
            Snackbar.LENGTH_SHORT
        ).show()
    }
    binding.resetButton.setOnClickListener {
        resetScores()
    }
}

private fun addPoint(isFirstTeam: Boolean) {
    if (isFirstTeam) {
        val newPoints = (firstTeamPoints + 1).toString()
        binding.firstTeamScore.text = newPoints
    } else {
        val newPoints = (secondTeamPoints + 1).toString()
        binding.secondTeamScore.text = newPoints
    }
}

private fun resetScores() {
    firstTeamPoints = 0
    secondTeamPoints = 0
    binding.firstTeamScore.text = "0"
    binding.secondTeamScore.text = "0"
}

private fun checkForVictory() {
    if ((firstTeamPoints >= secondTeamPoints + requiredDifference && firstTeamPoints >= 10) ||
        (secondTeamPoints >= firstTeamPoints + requiredDifference && secondTeamPoints >= 10)
    ) {

    }
}

private fun finishButton() {
    lifecycleScope.launch {
        val matchId = intent.getStringExtra(ID_MATCH_RB)
        if (matchId != null) {
            val match = dbAccess.matchDao().getMatchById(matchId)
            val idCup = match.idCup.toString()
            match.firstTeamPoints = firstTeamPoints
            match.secondTeamPoints = secondTeamPoints
            dbAccess.matchDao().update(match)
            val firstTeam = Converters().toTeam(match.firstTeamJson)
            val secondTeam = Converters().toTeam(match.secondTeamJson)
            firstTeam.inGamePoints = firstTeamPoints
            secondTeam.inGamePoints = secondTeamPoints
            dbAccess.teamDao().update(firstTeam)
            dbAccess.teamDao().update(secondTeam)
            changeToActivityCupInsight(idCup)
        }
    }
}


private fun changeToActivityCupInsight(idCup: String) {
    val activityCupInsight = Intent(this, ActivityCupInsight::class.java)
    activityCupInsight.putExtra(ID_CUP_CI, idCup)

    startActivity(activityCupInsight)
    finish()
}

}
