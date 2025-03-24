package com.example.scoreit.controller.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.R
import com.example.scoreit.model.components.Team
import com.example.scoreit.model.database.AppDataBase
import com.example.scoreit.model.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityNewTeamSettingsBinding
import kotlinx.coroutines.launch

class ActivityNewTeamSettings : AppCompatActivity() {
    // Binding para la actividad de configuración de nuevo equipo
    private lateinit var binding: ActivityNewTeamSettingsBinding

    // Acceso a la base de datos
    private lateinit var dbAccess: AppDataBase

    // Objeto companion para definir constantes
    companion object {
        const val ID_CUP_NT: String = "ID_CUP" // Clave para pasar el ID de la copa entre actividades
        const val ID_TEAM_NT: String = "ID_TEAM" // Clave para pasar el ID del equipo entre actividades
    }

    // Se llama a onCreate cuando la actividad es creada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el layout usando view binding
        binding = ActivityNewTeamSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el acceso a la base de datos
        dbAccess = getDatabase(this)

        // Configurar la cabecera con los botones de usuario y copa
        setHeader()

        // Establecer los datos en la UI
        setData()

        // Configurar el botón de retroceso
        backButton()

        // Configurar el botón de guardar
        saveButton()

        // Configurar el botón de eliminación
        deleteButton()
    }

    // Función para configurar la cabecera con los botones de usuario y copa
    private fun AppCompatActivity.setHeader() {
        val userButton = findViewById<ImageView>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)
        val idTeam = intent.getStringExtra(ID_TEAM_NT)
        val idCup = intent.getStringExtra(ID_CUP_NT)

        // Configurar el listener del botón de usuario
        userButton.setOnClickListener {
            if (idTeam == null && idCup != null) {
                lifecycleScope.launch {
                    val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    deleteCup(idCup) // Eliminar la copa
                    changeToActivityChangeUserData(idUser) // Navegar a la actividad de cambio de datos de usuario
                }
            } else if (idTeam != null && idCup == null) {
                lifecycleScope.launch {
                    val newIdCup = dbAccess.teamDao().getTeamById(idTeam).idCup.toString()
                    val idUser = dbAccess.cupDao().getCupById(newIdCup).idUser.toString()
                    deleteCup(newIdCup) // Eliminar la copa
                    changeToActivityChangeUserData(idUser) // Navegar a la actividad de cambio de datos de usuario
                }
            }
        }

        // Configurar el listener del botón de copa
        scoreItCup.setOnClickListener {
            if (idTeam == null && idCup != null) {
                lifecycleScope.launch {
                    val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    deleteCup(idCup) // Eliminar la copa
                    changeToActivityMainMenu(idUser) // Navegar al menú principal
                }
            } else if (idTeam != null && idCup == null) {
                lifecycleScope.launch {
                    val newIdCup = dbAccess.teamDao().getTeamById(idTeam).idCup.toString()
                    val idUser = dbAccess.cupDao().getCupById(newIdCup).idUser.toString()
                    deleteCup(newIdCup) // Eliminar la copa
                    changeToActivityMainMenu(idUser) // Navegar al menú principal
                }
            }
        }
    }

    // Función para configurar el botón de retroceso
    private fun backButton() {
        binding.backButton.setOnClickListener {
            val idCup = intent.getStringExtra(ID_CUP_NT)
            val idTeam = intent.getStringExtra(ID_TEAM_NT)
            if (idCup != null) {
                changeToActivityAddTeam(idCup) // Navegar a la actividad de agregar equipo
            } else if (idTeam != null) {
                lifecycleScope.launch {
                    val team = dbAccess.teamDao().getTeamById(idTeam)
                    val newIdCup = team.idCup.toString()
                    changeToActivityAddTeam(newIdCup) // Navegar a la actividad de agregar equipo
                }
            }
        }
    }

    // Función para configurar el botón de eliminación
    private fun deleteButton() {
        val idTeam = intent.getStringExtra(ID_TEAM_NT)
        val idCup = intent.getStringExtra(ID_CUP_NT)
        binding.deleteButton.setOnClickListener {
            if (idTeam != null && idCup == null) {
                deleteTeam(idTeam) // Eliminar el equipo
            }
        }
    }

    // Función para configurar el botón de guardar
    private fun saveButton() {
        binding.saveButton.setOnClickListener {
            val idTeam = intent.getStringExtra(ID_TEAM_NT)
            val idCup = intent.getStringExtra(ID_CUP_NT)
            if (idTeam == null && idCup != null) {
                addTeam(idCup) // Agregar un nuevo equipo
            }
            if (idTeam != null && idCup == null) {
                updateTeam() // Actualizar el equipo existente
            }
        }
    }

    // Función para eliminar un equipo
    private fun deleteTeam(idTeam: String) {
        lifecycleScope.launch {
            val actualIdCup = dbAccess.teamDao().getTeamById(idTeam).idCup.toString()
            dbAccess.teamDao().deleteById(idTeam) // Eliminar el equipo de la base de datos
            changeToActivityAddTeam(actualIdCup) // Navegar a la actividad de agregar equipo
        }
    }

    // Función para eliminar una copa
    private fun deleteCup(idCup: String) {
        lifecycleScope.launch {
            dbAccess.cupDao().deleteById(idCup) // Eliminar la copa de la base de datos
        }
    }

    // Función para establecer los datos en la UI
    private fun setData() {
        val idTeam = intent.getStringExtra(ID_TEAM_NT)
        val idCup = intent.getStringExtra(ID_CUP_NT)
        if (idTeam != null && idCup == null) {
            lifecycleScope.launch {
                val name = dbAccess.teamDao().getTeamById(idTeam).name
                binding.newTeamName.setText(name) // Establecer el nombre del equipo en la UI
                binding.deleteButton.visibility = View.VISIBLE // Mostrar el botón de eliminación
            }
        } else if (idTeam == null && idCup != null) {
            lifecycleScope.launch {
                val name = "Equipo ${dbAccess.teamDao().getTeamsByCupId(idCup).size + 1}"
                binding.newTeamName.setText(name) // Establecer un nombre predeterminado para el nuevo equipo
                binding.deleteButton.visibility = View.GONE // Ocultar el botón de eliminación
            }
        }
    }

    // Función para agregar un nuevo equipo
    private fun addTeam(idCup: String) {
        lifecycleScope.launch {
            val cup = dbAccess.cupDao().getCupById(idCup)
            val rounds = cup.requiredRounds
            val name = binding.newTeamName.text.toString().ifBlank {
                val num = dbAccess.teamDao().getTeamsByCupId(idCup.toString()).size + 1
                "Equipo $num" // Asignar un nombre predeterminado si no se proporciona
            }
            val newTeam =
                Team(name = name, idCup = idCup.toInt(), roundsWon = rounds, roundsLost = rounds)
            dbAccess.teamDao().insert(newTeam) // Insertar el nuevo equipo en la base de datos
            changeToActivityAddTeam(idCup.toString()) // Navegar a la actividad de agregar equipo
        }
    }

    // Función para actualizar un equipo existente
    private fun updateTeam() {
        val idTeam = intent.getStringExtra(ID_TEAM_NT)
        if (idTeam != null) {
            lifecycleScope.launch {
                val team = dbAccess.teamDao().getTeamById(idTeam)

                val name = binding.newTeamName.text.toString().ifBlank {
                    team.name // Mantener el nombre actual si no se proporciona uno nuevo
                }

                team.name = name
                dbAccess.teamDao().update(team) // Actualizar el equipo en la base de datos
                changeToActivityAddTeam(team.idCup.toString()) // Navegar a la actividad de agregar equipo
            }
        }
    }

    // Función para navegar a la actividad de agregar equipo
    private fun changeToActivityAddTeam(idCup: String) {
        val activityAddTeam = Intent(this, ActivityAddTeam::class.java)
        activityAddTeam.putExtra(ActivityAddTeam.ID_CUP_AT, idCup)

        startActivity(activityAddTeam)
        finish() // Finalizar la actividad actual
    }

    // Función para navegar al menú principal
    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ActivityMainMenu.ID_USER_MM, idUser)
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish() // Finalizar la actividad actual
    }

    // Función para navegar a la actividad de cambio de datos de usuario
    private fun changeToActivityChangeUserData(idUser: String) {
        val activityChangeUserData = Intent(this, ActivityChangeUserData::class.java)
        activityChangeUserData.putExtra(ActivityChangeUserData.ID_USER_CUD, idUser)

        startActivity(activityChangeUserData)
        finish() // Finalizar la actividad actual
    }
}