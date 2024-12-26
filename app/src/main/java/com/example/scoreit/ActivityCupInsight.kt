package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreit.ActivityMainMenu.Companion.ID_USER_MM
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
        const val ID_USER_CI: String = "ID_USER"
        const val ID_CUP_CI: String = "ID_CUP"
    }

    private val idUser = intent.getStringExtra(ID_USER_CI)
    private val idCup = intent.getStringExtra(ID_CUP_CI)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCupInsightBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        dbAccess = getDatabase(this)

        configurarEncabezado()
        setUpNombreDelCampeonato()
        setUpRecyclerScoreBoard()
        setUpRecyclerViewMatches()
        volverMenuPrincipal()

    }

    private fun AppCompatActivity.configurarEncabezado() {
//        val botonUsuario = findViewById<Button>(R.id.boton_de_usuario)
//        val copaScoreIt = findViewById<ImageView>(R.id.copa_score_it)
//
//        botonUsuario.setOnClickListener {
//            val intent = Intent(this, ActivityLogIn::class.java)
//            startActivity(intent)
//        }
//
//        copaScoreIt.setOnClickListener {
//            val activityVolverMenuPrincipal = Intent(this, ActivityMainMenu::class.java)
//            activityVolverMenuPrincipal.putExtra(USER_EMAIL, intent.getStringExtra(USER_EMAIL_DC))
//            startActivity(activityVolverMenuPrincipal)
//        }
    }

    private fun volverMenuPrincipal() {
        binding.backButton.setOnClickListener {
            val activityVolverMenuPrincipal = Intent(this, ActivityMainMenu::class.java)
            activityVolverMenuPrincipal.putExtra(ID_USER_MM, idUser)
            startActivity(activityVolverMenuPrincipal)
        }
    }

    private fun setUpNombreDelCampeonato() {
        lifecycleScope.launch {
            if (idCup != null) {
                val cup = dbAccess.cupDao().getCupById(idCup)
                binding.cupName.text = cup.name
//                binding.partidosText.text = "${binding.partidosText.text} ${
//                    dbAccess.partidoDao().obtenerPartidosPorId(campeonatoId).size
//                }"
//                binding.fechaText.text = "${binding.fechaText.text} ${
//                    dbAccess.campeonatoDao().obtenerPorId(campeonatoId).fechaDeInicio
//                }"
//                binding.modoDeJuegoText.text = "${binding.modoDeJuegoText.text}${
//                    dbAccess.campeonatoDao().obtenerPorId(campeonatoId).modoDeJuego
//                }"
//                binding.doblePartidoText.text = "${binding.doblePartidoText.text} ${
//                    dbAccess.campeonatoDao().obtenerPorId(campeonatoId).idaYVuelta
//                }"
//                binding.porRondasText.text = "${binding.porRondasText.text} ${
//                    dbAccess.campeonatoDao().obtenerPorId(campeonatoId).permisoDeRonda
//                }"
//                binding.siempreUnGanadorText.text = "${binding.siempreUnGanadorText.text} ${
//                    dbAccess.campeonatoDao().obtenerPorId(campeonatoId).siempreUnGanador
//                }"
//                binding.tiempoText.text = "${binding.tiempoText.text} ${
//                    dbAccess.campeonatoDao().obtenerPorId(campeonatoId).seJuegaPorTiempoMaximo
//                }"
//                binding.puntosText.text = "${binding.puntosText.text} ${
//                    dbAccess.campeonatoDao().obtenerPorId(campeonatoId).seJuegaPorPuntosMaximos
//                }"
            }
        }
    }


    private fun setUpRecyclerViewMatches() {
        if (idCup != null) {
            lifecycleScope.launch {
                val listOfMatches = dbAccess.matchDao().getMatchesByCupId(idCup).toMutableList()
                recyclerMatches.addDataToList(listOfMatches)

                binding.recyclerPartidosCreados.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = recyclerMatches
                }
            }
        }
    }

    private fun setUpRecyclerScoreBoard() {
        if (idCup != null) {
            lifecycleScope.launch {
                val listOfTeams =
                    dbAccess.teamDao().getTeamsByCupId(idCup).toMutableList()
                recyclerScoreBoardRows.addDataToList(listOfTeams)

                binding.recyclerScoreBoard.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = recyclerScoreBoardRows
                }
            }
        }
    }
}