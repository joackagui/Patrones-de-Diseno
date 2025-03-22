package com.example.scoreit.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.R
import com.example.scoreit.components.Cup
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.database.Converters
import com.example.scoreit.databinding.ActivityNewCupSettingsBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class ActivityNewCupSettings : AppCompatActivity() {
    // Binding para la actividad de configuración de nueva copa
    private lateinit var binding: ActivityNewCupSettingsBinding

    // Diálogo para seleccionar la fecha
    private lateinit var datePickerDialog: DatePickerDialog

    // Acceso a la base de datos
    private lateinit var dbAccess: AppDataBase

    // Objeto companion para definir constantes
    companion object {
        const val ID_USER_NC: String = "ID_USER" // Clave para pasar el ID del usuario entre actividades
        const val CUP_JSON_NC: String = "CUP_JSON" // Clave para pasar la copa en formato JSON
        const val ID_CUP_NC: String = "ID_CUP" // Clave para pasar el ID de la copa entre actividades
    }

    // Se llama a onCreate cuando la actividad es creada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el layout usando view binding
        binding = ActivityNewCupSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el acceso a la base de datos
        dbAccess = getDatabase(this)

        // Configurar la cabecera con los botones de usuario y copa
        setHeader()

        // Establecer los datos por defecto en la UI
        defaultData()

        // Obtener los datos de la copa si se proporcionan
        getData()

        // Configurar el botón de eliminación
        deleteButton()

        // Configurar el botón de retroceso
        backButton()

        // Configurar el botón de guardar
        saveButton()
    }

    // Función para establecer los datos por defecto en la UI
    private fun defaultData() {
        configureSwitches() // Configurar los interruptores
        configureDatePicker() // Configurar el selector de fecha
        roundsCheckbox() // Configurar la casilla de verificación de rondas
    }

    // Función para configurar la cabecera con los botones de usuario y copa
    private fun AppCompatActivity.setHeader() {
        val userButton = findViewById<ImageView>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)

        // Configurar el listener del botón de usuario
        userButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_NC)
            if (idUser != null) {
                changeToActivityChangeUserData(idUser) // Navegar a la actividad de cambio de datos de usuario
            }
        }

        // Configurar el listener del botón de copa
        scoreItCup.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_NC)
            val idCup = intent.getStringExtra(ID_CUP_NC)
            val cupJson = intent.getStringExtra(CUP_JSON_NC)
            if (idUser != null) {
                changeToActivityMainMenu(idUser) // Navegar al menú principal
            } else if (idCup != null) {
                lifecycleScope.launch {
                    val newIdUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    changeToActivityMainMenu(newIdUser) // Navegar al menú principal con el ID del usuario
                }
            } else if (cupJson != null) {
                lifecycleScope.launch {
                    val cup = Converters().toCup(cupJson)
                    val newIdUser = cup.idUser.toString()
                    changeToActivityMainMenu(newIdUser) // Navegar al menú principal con el ID del usuario
                }
            }
        }
    }

    // Función para obtener los datos de la copa si se proporcionan
    private fun getData() {
        val idUser = intent.getStringExtra(ID_USER_NC)
        val idCup = intent.getStringExtra(ID_CUP_NC)
        val cupJson = intent.getStringExtra(CUP_JSON_NC)

        if (idUser == null && idCup != null && cupJson == null) {
            lifecycleScope.launch {
                val cup = dbAccess.cupDao().getCupById(idCup)
                setData(cup = cup, hasStarted = cup.hasStarted, wasCreated = true) // Establecer los datos de la copa en la UI
            }
        }
        if (idUser == null && idCup == null && cupJson != null) {
            val cup = Converters().toCup(cupJson)
            setData(cup = cup, hasStarted = false, wasCreated = false) // Establecer los datos de la copa en la UI
        }
    }

    // Función para establecer los datos de la copa en la UI
    private fun setData(cup: Cup, hasStarted: Boolean, wasCreated: Boolean) {
        val newHeaderText = "Edita tu copa"
        binding.currentScreenInfo.text = newHeaderText
        binding.newCupName.setText(cup.name)
        binding.newStartDate.text = cup.startDate
        binding.requiredPoints.setText(cup.requiredPoints.toString())
        binding.doubleMatchCheckbox.isChecked = cup.doubleMatch
        binding.alwaysWinnerCheckbox.isChecked = cup.alwaysWinner
        binding.twoPointsDifferenceCheckbox.isChecked = cup.twoPointsDifference

        if (cup.requiredRounds != null) {
            binding.roundsAmountNumberPicker.value = cup.requiredRounds!!
        } else {
            binding.roundsAmountNumberPicker.value = 2
        }

        if (wasCreated) {
            binding.deleteButton.visibility = View.VISIBLE
            binding.doubleMatchCheckbox.visibility = View.GONE

            if (hasStarted) {
                binding.requiredPoints.visibility = View.GONE
                binding.twoPointsDifferenceCheckbox.visibility = View.GONE
                binding.roundsCheckbox.visibility = View.GONE
                binding.alwaysWinnerCheckbox.visibility = View.GONE
                binding.roundsAmountText.visibility = View.GONE
                binding.roundsAmountNumberPicker.visibility = View.GONE
                binding.doubleMatchCheckbox.visibility = View.GONE
            }
        }
    }

    // Función para crear una nueva copa
    private fun createCup(idUser: String) {
        var name = binding.newCupName.text.toString()
        val startDate = binding.newStartDate.text.toString()
        var requiredPoints = 0
        var requiredRounds: Int? = null
        val doubleMatch = binding.doubleMatchCheckbox.isChecked
        val alwaysWinner = binding.alwaysWinnerCheckbox.isChecked
        val twoPointsDifference = if (binding.alwaysWinnerCheckbox.isChecked) binding.twoPointsDifferenceCheckbox.isChecked else false

        lifecycleScope.launch {
            if (binding.newCupName.text.toString().isBlank()) {
                val cupCount = dbAccess.cupDao().getCupsByUserId(idUser).size
                name = "Copa ${cupCount + 1}" // Asignar un nombre predeterminado si no se proporciona
            }

            if (binding.requiredPoints.text.toString() != "" && binding.requiredPoints.text.toString().toInt() != 0) {
                requiredPoints = binding.requiredPoints.text.toString().toInt()
            }

            if (binding.roundsCheckbox.isChecked) {
                requiredRounds = binding.roundsAmountNumberPicker.value
            }

            val newCup = Cup(
                name = name,
                startDate = startDate,
                requiredPoints = requiredPoints,
                requiredRounds = requiredRounds,
                doubleMatch = doubleMatch,
                alwaysWinner = alwaysWinner,
                twoPointsDifference = twoPointsDifference,
                idUser = idUser.toInt(),
                winner = null,
            )

            if (binding.requiredPoints.isEnabled && binding.requiredPoints.text.toString() == "") {
                errorMessage() // Mostrar mensaje de error si no se proporcionan puntos
            } else {
                val idCup = dbAccess.cupDao().insert(newCup).toInt()
                changeToActivityAddTeam(idCup.toString()) // Navegar a la actividad de agregar equipo
            }
        }
    }

    // Función para configurar el botón de retroceso
    private fun backButton() {
        binding.backButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_NC)
            val idCup = intent.getStringExtra(ID_CUP_NC)
            val cupJson = intent.getStringExtra(CUP_JSON_NC)
            if (idUser != null && idCup == null && cupJson == null) {
                changeToActivityMainMenu(idUser) // Navegar al menú principal
            }
            if (idUser == null && idCup != null && cupJson == null) {
                changeToActivityCupInsight(idCup) // Navegar a la actividad de visualización de la copa
            }
            if (idUser == null && idCup == null && cupJson != null) {
                val cup = Converters().toCup(cupJson)
                val newIdUser = cup.idUser.toString()
                changeToActivityMainMenu(newIdUser) // Navegar al menú principal
            }
        }
    }

    // Función para configurar el botón de eliminación
    private fun deleteButton() {
        binding.deleteButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_NC)
            val idCup = intent.getStringExtra(ID_CUP_NC)
            val cupJson = intent.getStringExtra(CUP_JSON_NC)
            if (idUser == null && idCup != null && cupJson == null) {
                lifecycleScope.launch {
                    val newIdUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    dbAccess.matchDao().deleteMatchesByIdCup(idCup) // Eliminar partidos asociados
                    dbAccess.teamDao().deleteTeamsByIdCup(idCup) // Eliminar equipos asociados
                    dbAccess.cupDao().deleteById(idCup) // Eliminar la copa
                    changeToActivityMainMenu(newIdUser) // Navegar al menú principal
                }
            }
        }
    }

    // Función para configurar el botón de guardar
    private fun saveButton() {
        binding.saveButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_NC)
            val idCup = intent.getStringExtra(ID_CUP_NC)
            val cupJson = intent.getStringExtra(CUP_JSON_NC)

            if (idUser != null && idCup == null) {
                createCup(idUser) // Crear una nueva copa
            }
            if (idCup == null && cupJson != null) {
                val cup = Converters().toCup(cupJson)
                createCup(cup.idUser.toString()) // Crear una nueva copa
            }
            if (idCup != null) {
                updateCup(idCup) // Actualizar la copa existente
            }
        }
    }

    // Función para actualizar una copa existente
    private fun updateCup(idCup: String) {
        lifecycleScope.launch {
            val cup = dbAccess.cupDao().getCupById(idCup)

            if (!cup.hasStarted) {
                var name = binding.newCupName.text.toString()
                val startDate = binding.newStartDate.text.toString()
                var winningPoints = 0
                var requiredRounds: Int? = null
                val doubleMatch = binding.doubleMatchCheckbox.isChecked
                val alwaysWinner = binding.alwaysWinnerCheckbox.isChecked
                val twoPointsDifference = binding.twoPointsDifferenceCheckbox.isChecked

                val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                if (binding.newCupName.text.toString().isBlank()) {
                    val cupCount = dbAccess.cupDao().getCupsByUserId(idUser).size
                    name = "Copa $cupCount" // Asignar un nombre predeterminado si no se proporciona
                }

                if (binding.requiredPoints.isEnabled && binding.requiredPoints.text.toString() != "") {
                    winningPoints = binding.requiredPoints.text.toString().toInt()
                }

                if (binding.roundsCheckbox.isChecked) {
                    requiredRounds = binding.roundsAmountNumberPicker.value
                }

                val newCup = Cup(
                    id = idCup.toInt(),
                    name = name,
                    startDate = startDate,
                    requiredPoints = winningPoints,
                    requiredRounds = requiredRounds,
                    doubleMatch = doubleMatch,
                    alwaysWinner = alwaysWinner,
                    twoPointsDifference = twoPointsDifference,
                    idUser = idUser.toInt(),
                    winner = null,
                )

                if (binding.requiredPoints.isEnabled && binding.requiredPoints.text.toString() == "" && binding.requiredPoints.text.toString().toInt() != 0) {
                    errorMessage() // Mostrar mensaje de error si no se proporcionan puntos
                } else {
                    dbAccess.cupDao().update(newCup) // Actualizar la copa en la base de datos
                }
            } else {
                val name = binding.newCupName.text.toString().ifBlank {
                    cup.name
                }
                cup.name = name
                dbAccess.cupDao().update(cup) // Actualizar el nombre de la copa
            }
            changeToActivityCupInsight(idCup) // Navegar a la actividad de visualización de la copa
        }
    }

    // Función para configurar la casilla de verificación de rondas
    private fun roundsCheckbox() {
        binding.roundsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.roundsAmountText.visibility = visibility
            binding.roundsAmountNumberPicker.visibility = visibility
        }
    }

    // Función para configurar los interruptores
    private fun configureSwitches() {
        binding.requiredPoints.isEnabled = true

        binding.twoPointsDifferenceCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.alwaysWinnerCheckbox.isChecked = true
            }
        }

        binding.alwaysWinnerCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.twoPointsDifferenceCheckbox.apply {
                    isEnabled = true
                    setTextColor(ContextCompat.getColor(context, R.color.White))
                }
            } else {
                binding.alwaysWinnerCheckbox.isChecked = false
                binding.twoPointsDifferenceCheckbox.apply {
                    isEnabled = false
                    setTextColor(ContextCompat.getColor(context, R.color.TransparentWhite))
                }
            }
        }
    }

    // Función para configurar el selector de fecha
    private fun configureDatePicker() {
        initDatePicker() // Inicializar el selector de fecha
        binding.newStartDate.text = getCurrentDate() // Establecer la fecha actual en la UI

        binding.newStartDate.setOnClickListener {
            openDatePicker() // Abrir el selector de fecha al hacer clic
        }
    }

    // Función para inicializar el selector de fecha
    private fun initDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val formattedMonth = month + 1
            val date = makeDateString(day, formattedMonth, year) // Formatear la fecha seleccionada
            binding.newStartDate.text = date // Establecer la fecha seleccionada en la UI
        }

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val style = R.style.CustomDatePickerDialog
        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
    }

    // Función para abrir el selector de fecha
    private fun openDatePicker() {
        datePickerDialog.show()
    }

    // Función para obtener la fecha actual
    private fun getCurrentDate(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year) // Formatear la fecha actual
    }

    // Función para formatear la fecha como una cadena de texto
    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return "${getMonthFormat(month)} $day $year"
    }

    // Función para obtener el nombre del mes en formato abreviado
    private fun getMonthFormat(month: Int): String {
        return when (month) {
            1 -> "ENE"
            2 -> "FEB"
            3 -> "MAR"
            4 -> "ABR"
            5 -> "MAY"
            6 -> "JUN"
            7 -> "JUL"
            8 -> "AGO"
            9 -> "SEP"
            10 -> "OCT"
            11 -> "NOV"
            12 -> "DIC"
            else -> "ENE"
        }
    }

    // Función para mostrar un mensaje de error
    private fun errorMessage() {
        Toast.makeText(this, "Debes ingresar los puntos", Toast.LENGTH_LONG).show()
    }

    // Función para navegar a la actividad de agregar equipo
    private fun changeToActivityAddTeam(idCup: String) {
        val activityAddTeam = Intent(this, ActivityAddTeam::class.java)
        activityAddTeam.putExtra(ActivityAddTeam.Companion.ID_CUP_AT, idCup)

        startActivity(activityAddTeam)
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

    // Función para navegar a la actividad de la copa seleccionada
    private fun changeToActivityCupInsight(idCup: String) {
        val activityCupInsight = Intent(this, ActivityCupInsight::class.java)
        activityCupInsight.putExtra(ActivityCupInsight.Companion.ID_CUP_CI, idCup)

        startActivity(activityCupInsight)
        finish()
    }
}