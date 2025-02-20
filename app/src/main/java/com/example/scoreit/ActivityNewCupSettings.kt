package com.example.scoreit

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.ActivityAddTeam.Companion.ID_CUP_AT
import com.example.scoreit.ActivityChangeUserData.Companion.ID_USER_CUD
import com.example.scoreit.ActivityCupInsight.Companion.ID_CUP_CI
import com.example.scoreit.ActivityMainMenu.Companion.ID_USER_MM
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
    private var logo: String? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                logo = uri.toString()
            }
        }

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

        //addLogoButton()
        deleteButton()
        backButton()
        saveButton()
    }

    private fun addLogoButton() {
        binding.addLogoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun defaultData() {
        configureSwitches()
        configureDatePicker()
        configureSpinner()
        restingCheckbox()
        roundsCheckbox()
    }

    private fun AppCompatActivity.setHeader() {
        val userButton = findViewById<ImageView>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)

        userButton.setOnClickListener {
            changeToActivityChangeUserData()
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
        binding.requiredTime.setText(cup.requiredTime.toString())
        binding.doubleMatchCheckbox.isChecked = cup.doubleMatch
        binding.alwaysWinnerCheckbox.isChecked = cup.alwaysWinner
        binding.twoPointsDifferenceCheckbox.isChecked = cup.twoPointsDifference

        if (cup.restingTime != null && cup.restingAmount != null) {
            binding.restingCheckbox.isChecked = true
            binding.restingMinutesNumberPicker.isEnabled = true
            binding.restingAmountNumberPicker.isEnabled = true
            binding.restingMinutesText.visibility = View.VISIBLE
            binding.restingMinutesNumberPicker.visibility = View.VISIBLE
            binding.restingAmountText.visibility = View.VISIBLE
            binding.restingAmountNumberPicker.visibility = View.VISIBLE
            binding.restingMinutesNumberPicker.value = cup.restingTime!!
            binding.restingAmountNumberPicker.value = cup.restingAmount!!
        } else {
            binding.restingCheckbox.isChecked = false
            binding.restingMinutesNumberPicker.value = 1
            binding.restingAmountNumberPicker.value = 1
            binding.restingMinutesText.visibility = View.GONE
            binding.restingMinutesNumberPicker.visibility = View.GONE
        }
        if (cup.requiredRounds != null) {
            binding.roundsCheckbox.isChecked = true
            binding.roundsAmountNumberPicker.isEnabled = true
            binding.roundsAmountNumberPicker.value = cup.requiredRounds!!
            binding.roundsAmountText.visibility = View.VISIBLE
            binding.roundsAmountNumberPicker.visibility = View.VISIBLE
        } else {
            binding.roundsCheckbox.isChecked = false
            binding.roundsAmountText.visibility = View.GONE
            binding.roundsAmountNumberPicker.visibility = View.GONE
            binding.roundsAmountNumberPicker.value = 2
        }
        if (cup.requiredTime == null) {
            binding.timeSwitch.isChecked = false
            binding.requiredTime.isEnabled = false
            binding.requiredTime.text = null
        }
        if (cup.requiredPoints == null) {
            binding.pointsSwitch.isChecked = false
            binding.requiredPoints.isEnabled = false
            binding.requiredPoints.text = null
        }

        binding.gameModeSpinner.setSelection(
            resources.getStringArray(R.array.game_mode_options).indexOf(cup.gameMode)
        )

        if (wasCreated) {
            binding.deleteButton.visibility = View.VISIBLE
            binding.doubleMatchCheckbox.visibility = View.GONE
            binding.roundsCheckbox.visibility = View.GONE
            binding.gameModeSpinner.visibility = View.GONE

            if (hasStarted) {
                binding.requiredPoints.visibility = View.GONE
                binding.requiredTime.visibility = View.GONE
                binding.timeSwitch.visibility = View.GONE
                binding.pointsSwitch.visibility = View.GONE
                binding.twoPointsDifferenceCheckbox.visibility = View.GONE
                binding.restingCheckbox.visibility = View.GONE
                binding.roundsCheckbox.visibility = View.GONE
                binding.alwaysWinnerCheckbox.visibility = View.GONE
                binding.restingMinutesText.visibility = View.GONE
                binding.restingMinutesNumberPicker.visibility = View.GONE
                binding.restingAmountText.visibility = View.GONE
                binding.restingAmountNumberPicker.visibility = View.GONE
                binding.roundsAmountText.visibility = View.GONE
                binding.roundsAmountNumberPicker.visibility = View.GONE
                binding.gameModeSpinner.visibility = View.GONE
                binding.doubleMatchCheckbox.visibility = View.GONE
            }
        }
    }

    private fun createCup(idUser: String) {
        var name = binding.newCupName.text.toString()
        val startDate = binding.newStartDate.text.toString()
        var requiredPoints: Int? = null
        var requiredTime: Int? = null
        val gameMode = binding.gameModeSpinner.selectedItem.toString()
        var restingTime: Int? = null
        var restingAmount: Int? = null
        var requiredRounds: Int? = null
        val doubleMatch = binding.doubleMatchCheckbox.isChecked
        val alwaysWinner = if (doubleMatch) binding.alwaysWinnerCheckbox.isChecked else true

        val twoPointsDifference =
            binding.twoPointsDifferenceCheckbox.isChecked

        lifecycleScope.launch {
            if (binding.newCupName.text.toString().isBlank()) {
                val cupCount = dbAccess.cupDao().getCupsByUserId(idUser).size
                name = "Cup ${cupCount + 1}"
            }

            if (binding.requiredPoints.isEnabled && binding.requiredPoints.text.toString() != "") {
                requiredPoints = binding.requiredPoints.text.toString().toInt()
            }

            if (binding.requiredTime.isEnabled && binding.requiredTime.text.toString() != "") {
                requiredTime = binding.requiredTime.text.toString().toInt()
            }

            if (binding.restingCheckbox.isChecked) {
                restingTime = binding.restingMinutesNumberPicker.value
                restingAmount = binding.restingAmountNumberPicker.value
            }

            if (binding.roundsCheckbox.isChecked) {
                requiredRounds = binding.roundsAmountNumberPicker.value
            }

            if (logo == null) {
                logo = "drawable/cup_logo.jpg"
            }

            if (binding.requiredTime.isEnabled && binding.requiredPoints.isEnabled
                || !binding.requiredTime.isEnabled && !binding.requiredPoints.isEnabled
            ) {
                throw IllegalStateException("What have you done? you must only choose one option")
            }

            val newCup = Cup(
                name = name,
                logo = logo!!,
                startDate = startDate,
                requiredPoints = requiredPoints,
                requiredTime = requiredTime,
                gameMode = gameMode,
                restingTime = restingTime,
                restingAmount = restingAmount,
                requiredRounds = requiredRounds,
                doubleMatch = doubleMatch,
                alwaysWinner = alwaysWinner,
                twoPointsDifference = twoPointsDifference,
                idUser = idUser.toInt(),
                winner = null,
            )

            if (binding.requiredTime.isEnabled && binding.requiredTime.text.toString() == "") {
                errorMessage(1)
            } else if (binding.requiredPoints.isEnabled && binding.requiredPoints.text.toString() == "") {
                errorMessage(2)
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

            if (logo == null) {
                logo = cup.logo
            }

            if (!cup.hasStarted) {
                var name = binding.newCupName.text.toString()
                val startDate = binding.newStartDate.text.toString()
                var winningPoints: Int? = null
                var finishTime: Int? = null
                val gameMode = binding.gameModeSpinner.selectedItem.toString()
                var restingTime: Int? = null
                var restingAmount: Int? = null
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

                if (binding.requiredTime.isEnabled && binding.requiredTime.text.toString() != "") {
                    finishTime = binding.requiredTime.text.toString().toInt()
                }

                if (binding.restingCheckbox.isChecked) {
                    restingTime = binding.restingMinutesNumberPicker.value
                    restingAmount = binding.restingAmountNumberPicker.value
                }

                if (binding.roundsCheckbox.isChecked) {
                    requiredRounds = binding.roundsAmountNumberPicker.value
                }

                if (binding.requiredTime.isEnabled && binding.requiredPoints.isEnabled
                    || !binding.requiredTime.isEnabled && !binding.requiredPoints.isEnabled
                ) {
                    throw IllegalStateException("What have you done? you must only choose one option")
                }
                val newCup = Cup(
                    id = idCup.toInt(),
                    name = name,
                    startDate = startDate,
                    requiredPoints = winningPoints,
                    requiredTime = finishTime,
                    gameMode = gameMode,
                    restingTime = restingTime,
                    restingAmount = restingAmount,
                    requiredRounds = requiredRounds,
                    doubleMatch = doubleMatch,
                    alwaysWinner = alwaysWinner,
                    twoPointsDifference = twoPointsDifference,
                    idUser = idUser.toInt(),
                    winner = null,
                )

                if (binding.requiredTime.isEnabled && binding.requiredTime.text.toString() == "") {
                    errorMessage(1)
                } else if (binding.requiredPoints.isEnabled && binding.requiredPoints.text.toString() == "") {
                    errorMessage(2)
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

        binding.timeSwitch.isChecked = false
        binding.requiredTime.isEnabled = false
        binding.pointsSwitch.isChecked = true
        binding.requiredPoints.isEnabled = true

        binding.timeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.requiredTime.isEnabled = true

                binding.pointsSwitch.isChecked = false
                binding.requiredPoints.isEnabled = false
                binding.requiredPoints.text = null

                binding.twoPointsDifferenceCheckbox.visibility = View.GONE
            } else {
                if (!binding.pointsSwitch.isChecked) {
                    binding.timeSwitch.isChecked = true
                }
            }
        }

        binding.pointsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.requiredPoints.isEnabled = true
                binding.timeSwitch.isChecked = false
                binding.requiredTime.isEnabled = false
                binding.requiredTime.text = null

                binding.twoPointsDifferenceCheckbox.isChecked = false
                binding.twoPointsDifferenceCheckbox.visibility = View.VISIBLE
            } else {
                if (!binding.timeSwitch.isChecked) {
                    binding.pointsSwitch.isChecked = true
                }
            }
        }

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

    private fun configureSpinner() {
        val gameMode = resources.getStringArray(R.array.game_mode_options)
        val adapter = ArrayAdapter(this, R.layout.spinner_item_style, gameMode)
        binding.gameModeSpinner.adapter = adapter
    }

    private fun errorMessage(number: Int) {
        when (number) {
            1 -> {
                Toast.makeText(this, "You must insert Time", Toast.LENGTH_LONG).show()
            }

            2 -> {
                Toast.makeText(this, "You must insert Points", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun changeToActivityAddTeam(idCup: String) {
        val activityAddTeam = Intent(this, ActivityAddTeam::class.java)
        activityAddTeam.putExtra(ID_CUP_AT, idCup)

        startActivity(activityAddTeam)
    }

    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ID_USER_MM, idUser)
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish()
    }

    private fun changeToActivityChangeUserData() {
        val activityChangeUserData = Intent(this, ActivityChangeUserData::class.java)
        activityChangeUserData.putExtra(ID_USER_CUD, intent.getStringExtra(ID_USER_NC))

        startActivity(activityChangeUserData)
    }

    private fun changeToActivityCupInsight(idCup: String) {
        val activityCupInsight = Intent(this, ActivityCupInsight::class.java)
        activityCupInsight.putExtra(ID_CUP_CI, idCup)

        startActivity(activityCupInsight)
        finish()
    }
}