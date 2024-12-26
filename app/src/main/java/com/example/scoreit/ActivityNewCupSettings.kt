package com.example.scoreit

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.ActivityAddTeam.Companion.ID_CUP_AT
import com.example.scoreit.ActivityAddTeam.Companion.ID_USER_AT
import com.example.scoreit.ActivityMainMenu.Companion.ID_USER_MM
import com.example.scoreit.componentes.Cup
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityNewCupSettingsBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class ActivityNewCupSettings : AppCompatActivity() {

    private lateinit var binding: ActivityNewCupSettingsBinding
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_USER_NC: String = "ID_USER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCupSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbAccess = getDatabase(this)

        setHeader()
        saveButton()
        backButton()

        configureSwitches()
        configureDatePicker()
        configureSpinner()
        restingCheckbox()
        roundsCheckbox()
    }

    private fun AppCompatActivity.setHeader() {
//        val userButton = findViewById<Button>(R.id.user_button)
//        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)
//
//        userButton.setOnClickListener {
//            val intent = Intent(this, ActivityLogIn::class.java)
//            startActivity(intent)
//        }
//
//        scoreItCup.setOnClickListener {
//            val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
//            activityMainMenu.putExtra(USER_EMAIL, intent.getStringExtra(USER_EMAIL_NC))
//            startActivity(activityMainMenu)
//        }
    }

    private fun saveButton() {
        binding.saveButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_NC)
            if (idUser != null) {
                createCup(idUser)
            }
        }
    }

    private fun createCup(idUser: String) {
        lifecycleScope.launch {
            val name = binding.newCupName.text.toString()
            val startDate = binding.newStartDate.text.toString()
            var winningPoints: Int? = null
            var finishTime: Int? = null
            val gameMode = binding.gameModeSpinner.toString()
            var restingTime: Int? = null
            var restingAmount: Int? = null
            var roundsAmount: Int? = null
            val twoMatches = binding.twoMatchesCheckbox.text.toString().toBoolean()
            val alwaysWinner = binding.alwaysWinnerCheckbox.text.toString().toBoolean()
            val twoPointsDifference =
                binding.twoPointsDifferenceCheckbox.text.toString().toBoolean()

            if (binding.newMatchPoints.isEnabled) {
                winningPoints = binding.newMatchPoints.text.toString().toInt()
            }

            if (binding.newMatchTime.isEnabled) {
                finishTime = binding.newMatchPoints.text.toString().toInt()
            }

            if (binding.newMatchTime.isEnabled && binding.newMatchPoints.isEnabled
                || !binding.newMatchTime.isEnabled && !binding.newMatchPoints.isEnabled
            ) {
                throw IllegalStateException("What have you done? you must only choose one option")
            }

            if (binding.restingMinutesNumberPicker.isEnabled) {
                restingTime = binding.restingMinutesNumberPicker.value
            }

            if (binding.restingAmountNumberPicker.isEnabled) {
                restingAmount = binding.restingAmountNumberPicker.value
            }

            if (binding.roundsAmountNumberPicker.isEnabled) {
                roundsAmount = binding.roundsAmountNumberPicker.value
            }

            val newCup = Cup(
                selected = true,
                name = name,
                startDate = startDate,
                winningPoints = winningPoints,
                finishTime = finishTime,
                gameMode = gameMode,
                restingTime = restingTime,
                restingAmount = restingAmount,
                roundsAmount = roundsAmount,
                twoMatches = twoMatches,
                alwaysWinner = alwaysWinner,
                twoPointsDifference = twoPointsDifference,
                idUser = idUser.toInt()
            )
            dbAccess.cupDao().insert(newCup)
            message()
            //TODO changeToActivityAddTeam(idCup)
        }
    }

    private fun message() {
        Toast.makeText(this, "New Cup saved", Toast.LENGTH_LONG).show()
    }

    private fun changeToActivityAddTeam(idCup: Int) {
        val idUser = intent.getStringExtra(ID_USER_NC)
        if (idUser != null) {
            val activityAddTeam = Intent(this, ActivityAddTeam::class.java)
            activityAddTeam.putExtra(ID_USER_AT, idUser)
            activityAddTeam.putExtra(ID_CUP_AT, idCup.toString())
            startActivity(activityAddTeam)
        }
    }

    private fun backButton() {
        binding.backButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_NC)
            if (idUser != null) {
                val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
                activityMainMenu.putExtra(ID_USER_MM, idUser)
                startActivity(activityMainMenu)
            }
        }
    }

    private fun restingCheckbox() {
        binding.restingCheckbox.setOnCheckedChangeListener { _, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.restingMinutesText.visibility = visibility
            binding.restingMinutesNumberPicker.visibility = visibility

            binding.restingAmountText.visibility = visibility
            binding.restingAmountNumberPicker.visibility = visibility
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
        binding.timeSwitch.isUseMaterialThemeColors = false
        binding.pointsSwitch.isUseMaterialThemeColors = false

        binding.timeSwitch.isChecked = true
        binding.newMatchTime.isEnabled = true
        binding.pointsSwitch.isChecked = false
        binding.newMatchPoints.isEnabled = false

        binding.timeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.pointsSwitch.isChecked = false
                binding.newMatchTime.isEnabled = true
                binding.newMatchPoints.isEnabled = false
            } else {
                if (!binding.pointsSwitch.isChecked) {
                    binding.timeSwitch.isChecked = true
                }
            }
        }

        binding.pointsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.timeSwitch.isChecked = false
                binding.newMatchPoints.isEnabled = true
                binding.newMatchTime.isEnabled = false
            } else {
                if (!binding.timeSwitch.isChecked) {
                    binding.pointsSwitch.isChecked = true
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

    private fun configureSpinner() {
        val gameMode = resources.getStringArray(R.array.game_mode_options)
        val adapter = ArrayAdapter(this, R.layout.spinner_item_style, gameMode)
        binding.gameModeSpinner.adapter = adapter
    }

}
