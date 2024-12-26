package com.example.scoreit

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityRefereeButtonsBinding


class ActivityRefereeButtons : AppCompatActivity() {

    private lateinit var binding: ActivityRefereeButtonsBinding
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_USER_RB: String = "ID_USER"
        const val ID_CUP_RB: String = "ID_CUP"
        const val ID_MATCH_RB: String = "ID_MATCH"
    }

    private val idUser = intent.getStringExtra(ID_USER_RB)
    private val idCup = intent.getStringExtra(ID_CUP_RB)
    private val idMatch = intent.getStringExtra(ID_MATCH_RB)

    private var countDownTimer: CountDownTimer? = null
    private var timeInMillis: Long = 0L
    private var isRunning = false
    private var timeRemaining: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRefereeButtonsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbAccess = getDatabase(this)
//        lifecycleScope.launch {
//            evaluacionDelPartido()
//            obtenerTiempoDeJuego()
//            descanso()
//        }


//        aumentarPuntos()

//        binding.botonDescansoSegundoEquipo.setOnClickListener {
//                iniciarTemporizador()
//        }

        /*binding.startPauseButton.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        binding.resetButton.setOnClickListener {
            resetTimer()
        }*/

        //val input = dbAccess.campeonatoDao()

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
  /*  private fun startTimer() {
        // Verificar que haya un valor válido en el EditText
        /*val input = binding.input.text.toString()
        if (input.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa un tiempo en minutos", Toast.LENGTH_SHORT).show()
            return
        }*/

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
    } */

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
//    private fun evaluacionDelPartido() {
//        lifecycleScope.launch {
//            val idpartido: String = intent.getStringExtra(ID_PARTIDO_PA).toString()
//            val partidoActual =
//                dbAccess.partidoDao().obtenerPorId(idpartido)
//            val tieneMaximoDePuntos = dbAccess.partidoDao().obtenerSiPartidoPorPuntos(idpartido)
//            val maximoDePuntos = dbAccess.partidoDao()
//                .obtenerPuntosParaGanar(idpartido)
//            val tieneTiempoDeJuego = dbAccess.partidoDao()
//                .obtenerSiPartidoPorTiempo(idpartido)
//            val siempreGanador = dbAccess.partidoDao()
//                .obtenerSiempreUnGanador(idpartido)
//            var cantidadDescansosPrimerEquipo = dbAccess.partidoDao()
//                .obtenerCantidadDescansos(idpartido)
//            var cantidadDescansosSegundoEquipo = cantidadDescansosPrimerEquipo
//            var diferenciaDeDosPuntos = dbAccess.partidoDao()
//                .obtenerSiHayDiferenciaDeDosPuntos(idpartido)
//            var tiempoDelPartido = dbAccess.partidoDao()
//                .obtenerTiempoDelPartido(idpartido)
//
//            arbitrar(
//                partidoActual,
//                tieneMaximoDePuntos,
//                maximoDePuntos,
//                tieneTiempoDeJuego,
//                siempreGanador,
//                diferenciaDeDosPuntos,
//                cantidadDescansosPrimerEquipo,
//                cantidadDescansosSegundoEquipo,
//                tiempoDelPartido
//            )
//        }
//    }
//
//    private fun arbitrar(matchActual: Match,
//                         tieneMaximoDePuntos: Boolean,
//                         maximoDePuntos: Int,
//                         tieneTiempoDeJuego: Boolean,
//                         siempreGanador: Boolean,
//                         diferenciaDeDosPuntos: Boolean,
//                         cantidadDescansosPrimerEquipo: Int,
//                         cantidadDescansosSegundoEquipo: Int, tiempoDelPartido: Int) {
//        if (matchActual.puntosPrimerEquipo.toInt() >= maximoDePuntos || matchActual.puntosSegundoEquipo.toInt() >= maximoDePuntos) {
//            finalizar()
//            //stopTimer()
//        }
//        if (tieneTiempoDeJuego && tieneMaximoDePuntos && siempreGanador && diferenciaDeDosPuntos) {
//
//        } else if (tieneTiempoDeJuego && tieneMaximoDePuntos && siempreGanador && !diferenciaDeDosPuntos) {
//
//        } else if (tieneTiempoDeJuego && !tieneMaximoDePuntos && siempreGanador) {
//
//        } else if (tieneTiempoDeJuego && !tieneMaximoDePuntos && !siempreGanador) {
//
//        } else if (!tieneTiempoDeJuego && tieneMaximoDePuntos && siempreGanador) {
//            if(diferenciaDeDosPuntos){
//
//            } else {
//
//            }
//
//        } else if (!tieneTiempoDeJuego && tieneMaximoDePuntos) {
//            if(!diferenciaDeDosPuntos){
//
//            } else {
//
//            }
//
//        } else if (!tieneTiempoDeJuego && !tieneMaximoDePuntos && siempreGanador && !diferenciaDeDosPuntos) {
//            if(matchActual.puntosPrimerEquipo != matchActual.puntosSegundoEquipo){
//                finalizar()
//            }
//        } else if (!tieneTiempoDeJuego && !tieneMaximoDePuntos && !siempreGanador && !diferenciaDeDosPuntos) {
//            finalizar()
//        }
//    }
//
//    fun finalizarManualmente(){
//        lifecycleScope.launch {
//            binding.botonFinalizarPartido.setOnClickListener{
//                finalizar()
//            }
//        }
//    }
//
//    private fun finalizar() {
//
//    }
//
//    private fun aumentarPuntos() {
//        binding.botonPrimerEquipo.setOnClickListener {
//            binding.puntajePrimerEquipo.text =
//                (binding.puntajePrimerEquipo.text.toString().toInt() + 1).toString()
//        }
//        binding.botonSegundoEquipo.setOnClickListener {
//            binding.puntajeSegundoEquipo.text =
//                (binding.puntajeSegundoEquipo.text.toString().toInt() + 1).toString()
//        }
//    }
}

