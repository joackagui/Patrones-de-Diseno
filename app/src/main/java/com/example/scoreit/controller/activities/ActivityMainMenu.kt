package com.example.scoreit.controller.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreit.R
import com.example.scoreit.view.recyclers.RecyclerCups
import com.example.scoreit.model.database.AppDataBase
import com.example.scoreit.model.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityMainMenuBinding
import kotlinx.coroutines.launch

class ActivityMainMenu : AppCompatActivity() {

    // Inicialización perezosa del adaptador para la lista de copas
    private val recyclerCups: RecyclerCups by lazy { RecyclerCups() }

    // Binding para la actividad del menú principal
    private lateinit var binding: ActivityMainMenuBinding

    // Acceso a la base de datos
    private lateinit var dbAccess: AppDataBase

    // Objeto companion para definir constantes
    companion object {
        const val ID_USER_MM: String = "USER_ID" // Clave para pasar el ID del usuario entre actividades
    }

    // Se llama a onCreate cuando la actividad es creada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el layout usando view binding
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el acceso a la base de datos
        dbAccess = getDatabase(this)

        // Configurar la cabecera con los botones de usuario y copa
        setHeader()

        // Establecer el texto personalizado en la UI
        setText()

        // Configurar el RecyclerView para mostrar la lista de copas
        setUpRecyclerView()

        // Configurar el botón para crear una nueva copa
        newCupButton()
    }

    // Función para configurar la cabecera con los botones de usuario y copa
    private fun AppCompatActivity.setHeader() {
        val userButton = findViewById<ImageView>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)

        // Configurar el listener del botón de usuario
        userButton.setOnClickListener {
            changeToActivityChangeUserData() // Navegar a la actividad de cambio de datos de usuario
        }

        // Configurar el listener del botón de copa
        scoreItCup.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_MM)
            if (idUser != null) {
                changeToActivityMainMenu(idUser) // Navegar al menú principal
            }
        }
    }

    // Función para configurar el botón de creación de nueva copa
    private fun newCupButton() {
        val idUser = intent.getStringExtra(ID_USER_MM)
        if (idUser != null) {
            changeToNewCupSettings(idUser) // Navegar a la actividad de configuración de nueva copa
        }
    }

    // Función para establecer el texto personalizado en la UI
    private fun setText() {
        val idUser = intent.getStringExtra(ID_USER_MM)
        if (idUser != null) {
            lifecycleScope.launch {
                val userCup: TextView = binding.createdCupsText
                // Obtener el nombre del usuario desde la base de datos
                val userName = dbAccess.userDao().getUserById(idUser).name
                // Crear el texto personalizado
                val finalText = "Copas de ${userName}"
                userCup.text = finalText
            }
        }
    }

    // Función para configurar el RecyclerView con la lista de copas
    private fun setUpRecyclerView() {
        val idUser = intent.getStringExtra(ID_USER_MM)
        if (idUser != null) {
            lifecycleScope.launch {
                // Obtener la lista de copas asociadas al usuario
                val listOfCups = dbAccess.cupDao().getCupsByUserId(idUser).toMutableList()
                // Agregar los datos al adaptador
                recyclerCups.addDataToList(listOfCups)

                // Configurar el RecyclerView
                binding.recyclerCups.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = recyclerCups
                }
            }
        }
    }

    // Función para navegar a la actividad de configuración de nueva copa
    private fun changeToNewCupSettings(idUser: String) {
        binding.createNewCup.setOnClickListener {
            val activityNewCupSettings = Intent(this, ActivityNewCupSettings::class.java)
            activityNewCupSettings.putExtra(ActivityNewCupSettings.ID_USER_NC, idUser)

            startActivity(activityNewCupSettings)
        }
    }

    // Función para navegar a la actividad de cambio de datos de usuario
    private fun changeToActivityChangeUserData() {
        val activityChangeUserData = Intent(this, ActivityChangeUserData::class.java)
        activityChangeUserData.putExtra(
            ActivityChangeUserData.ID_USER_CUD,
            intent.getStringExtra(ID_USER_MM)
        )

        startActivity(activityChangeUserData)
    }

    // Función para navegar al menú principal
    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ID_USER_MM, idUser)
        // Limpiar la pila de actividades
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish() // Finalizar la actividad actual
    }
}