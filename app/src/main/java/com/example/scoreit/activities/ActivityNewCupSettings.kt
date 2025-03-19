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
    private lateinit var binding: ActivityNewCupSettingsBinding
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_USER_NC: String = "ID_USER"
        const val CUP_JSON_NC: String = "CUP_JSON"
        const val ID_CUP_NC: String = "ID_CUP"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCupSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbAccess = getDatabase(this)

        setHeader()
        defaultData()
        getData()

        deleteButton()
        backButton()
        saveButton()
    }

    private fun defaultData() {
        configureSwitches()
        configureDatePicker()
        roundsCheckbox()
    }

    private fun AppCompatActivity.setHeader() {
        val userButton = findViewById<ImageView>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)

        userButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_NC)
            if (idUser != null) {
                changeToActivityChangeUserData(idUser)
            }
        }

        scoreItCup.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_NC)
            val idCup = intent.getStringExtra(ID_CUP_NC)
            val cupJson = intent.getStringExtra(CUP_JSON_NC)
            if (idUser != null) {
                changeToActivityMainMenu(idUser)
            } else if (idCup != null) {
                lifecycleScope.launch {
                    val newIdUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    changeToActivityMainMenu(newIdUser)
                }
            } else if (cupJson != null) {
                lifecycleScope.launch {
                    val cup = Converters().toCup(cupJson)
                    val newIdUser = cup.idUser.toString()
                    changeToActivityMainMenu(newIdUser)
                }
            }
        }
    }

    private fun getData() {
        val idUser = intent.getStringExtra(ID_USER_NC)
        val idCup = intent.getStringExtra(ID_CUP_NC)
        val cupJson = intent.getStringExtra(CUP_JSON_NC)

        if (idUser == null && idCup != null && cupJson == null) {
            lifecycleScope.launch {
                val cup = dbAccess.cupDao().getCupById(idCup)
                setData(cup = cup, hasStarted = cup.hasStarted, wasCreated = true)
            }
        }
        if (idUser == null && idCup == null && cupJson != null) {
            val cup = Converters().toCup(cupJson)
            setData(cup = cup, hasStarted = false, wasCreated = false)
        }
    }

    private fun setData(cup: Cup, hasStarted: Boolean, wasCreated: Boolean) {
        val newHeaderText = "Edit Your Cup"
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

    private fun createCup(idUser: String) {
        var name = binding.newCupName.text.toString()
        val startDate = binding.newStartDate.text.toString()
        var requiredPoints: Int? = null
        var requiredRounds: Int? = null
        val doubleMatch = binding.doubleMatchCheckbox.isChecked
        val alwaysWinner = binding.alwaysWinnerCheckbox.isChecked
        val twoPointsDifference = if(binding.alwaysWinnerCheckbox.isChecked) binding.twoPointsDifferenceCheckbox.isChecked else false

        lifecycleScope.launch {
            if (binding.newCupName.text.toString().isBlank()) {
                val cupCount = dbAccess.cupDao().getCupsByUserId(idUser).size
                name = "Cup ${cupCount + 1}"
            }

            if (binding.requiredPoints.text.toString() != "") {
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
                errorMessage()
            } else {
                val idCup = dbAccess.cupDao().insert(newCup).toInt()
                changeToActivityAddTeam(idCup.toString())
            }
        }
    }

    private fun backButton() {
        binding.backButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_NC)
            val idCup = intent.getStringExtra(ID_CUP_NC)
            val cupJson = intent.getStringExtra(CUP_JSON_NC)
            if (idUser != null && idCup == null && cupJson == null) {
                changeToActivityMainMenu(idUser)
            }
            if (idUser == null && idCup != null && cupJson == null) {
                changeToActivityCupInsight(idCup)
            }
            if (idUser == null && idCup == null && cupJson != null) {
                val cup = Converters().toCup(cupJson)
                val newIdUser = cup.idUser.toString()
                changeToActivityMainMenu(newIdUser)
            }
        }
    }

    private fun deleteButton() {
        binding.deleteButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_NC)
            val idCup = intent.getStringExtra(ID_CUP_NC)
            val cupJson = intent.getStringExtra(CUP_JSON_NC)
            if (idUser == null && idCup != null && cupJson == null) {
                lifecycleScope.launch {
                    val newIdUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    dbAccess.matchDao().deleteMatchesByIdCup(idCup)
                    dbAccess.teamDao().deleteTeamsByIdCup(idCup)
                    dbAccess.cupDao().deleteById(idCup)
                    changeToActivityMainMenu(newIdUser)
                }
            }
        }
    }

    private fun saveButton() {
        binding.saveButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_NC)
            val idCup = intent.getStringExtra(ID_CUP_NC)
            val cupJson = intent.getStringExtra(CUP_JSON_NC)

            if (idUser != null && idCup == null) {
                createCup(idUser)
            }
            if (idCup == null && cupJson != null) {
                val cup = Converters().toCup(cupJson)
                createCup(cup.idUser.toString())
            }
            if (idCup != null) {
                updateCup(idCup)
            }
        }
    }

    private fun updateCup(idCup: String) {
        lifecycleScope.launch {
            val cup = dbAccess.cupDao().getCupById(idCup)

            if (!cup.hasStarted) {
                var name = binding.newCupName.text.toString()
                val startDate = binding.newStartDate.text.toString()
                var winningPoints: Int? = null
                var requiredRounds: Int? = null
                val doubleMatch = binding.doubleMatchCheckbox.isChecked
                val alwaysWinner = binding.alwaysWinnerCheckbox.isChecked
                val twoPointsDifference =
                    binding.twoPointsDifferenceCheckbox.isChecked

                val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                if (binding.newCupName.text.toString().isBlank()) {
                    val cupCount = dbAccess.cupDao().getCupsByUserId(idUser).size
                    name = "Cup $cupCount"
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

                if (binding.requiredPoints.isEnabled && binding.requiredPoints.text.toString() == "") {
                    errorMessage()
                } else {
                    dbAccess.cupDao().update(newCup)
                }
            } else {
                val name = binding.newCupName.text.toString().ifBlank {
                    cup.name
                }
                cup.name = name
                dbAccess.cupDao().update(cup)
            }
            changeToActivityCupInsight(idCup)
        }
    }

    private fun roundsCheckbox() {
        binding.roundsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.roundsAmountText.visibility = visibility
            binding.roundsAmountNumberPicker.visibility = visibility
        }
    }

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

    private fun configureDatePicker() {
        initDatePicker()
        binding.newStartDate.text = getCurrentDate()

        binding.newStartDate.setOnClickListener {
            openDatePicker()
        }
    }

    private fun initDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val formattedMonth = month + 1
            val date = makeDateString(day, formattedMonth, year)
            binding.newStartDate.text = date
        }

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val style = R.style.CustomDatePickerDialog
        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
    }

    private fun openDatePicker() {
        datePickerDialog.show()
    }

    private fun getCurrentDate(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return "${getMonthFormat(month)} $day $year"
    }

    private fun getMonthFormat(month: Int): String {
        return when (month) {
            1 -> "JAN"
            2 -> "FEB"
            3 -> "MAR"
            4 -> "APR"
            5 -> "MAY"
            6 -> "JUN"
            7 -> "JUL"
            8 -> "AUG"
            9 -> "SEP"
            10 -> "OCT"
            11 -> "NOV"
            12 -> "DEC"
            else -> "JAN"
        }
    }

    private fun errorMessage() {
        Toast.makeText(this, "You must insert Points", Toast.LENGTH_LONG).show()
    }

    private fun changeToActivityAddTeam(idCup: String) {
        val activityAddTeam = Intent(this, ActivityAddTeam::class.java)
        activityAddTeam.putExtra(ActivityAddTeam.Companion.ID_CUP_AT, idCup)

        startActivity(activityAddTeam)
    }

    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ActivityMainMenu.Companion.ID_USER_MM, idUser)
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish()
    }

    private fun changeToActivityChangeUserData(idUser: String) {
        val activityChangeUserData = Intent(this, ActivityChangeUserData::class.java)
        activityChangeUserData.putExtra(ActivityChangeUserData.Companion.ID_USER_CUD, idUser)

        startActivity(activityChangeUserData)
    }

    private fun changeToActivityCupInsight(idCup: String) {
        val activityCupInsight = Intent(this, ActivityCupInsight::class.java)
        activityCupInsight.putExtra(ActivityCupInsight.Companion.ID_CUP_CI, idCup)

        startActivity(activityCupInsight)
        finish()
    }
}