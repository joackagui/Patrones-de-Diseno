package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.ActivityAddTeam.Companion.ID_CUP_AT
import com.example.scoreit.components.Team
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityNewTeamSettingsBinding
import kotlinx.coroutines.launch

class ActivityNewTeamSettings : AppCompatActivity() {
    private lateinit var binding: ActivityNewTeamSettingsBinding
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_CUP_NT: String = "ID_CUP"
        const val ID_TEAM_NT: String = "ID_TEAM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTeamSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbAccess = getDatabase(this)

        setHeader()
        fillBlankSpaces()
        backButton()
        saveButton()
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
//            val idCup = intent.getStringExtra(ID_CUP_NT)
//            var idUser = ""
//            lifecycleScope.launch {
//                idUser = dbAccess.cupDao().getCupById(idCup.toString()).idUser.toString()
//            }
//            activityMainMenu.putExtra(ID_USER_MM, idUser)
//            startActivity(activityMainMenu)
//        }
    }

    private fun backButton() {
        binding.backButton.setOnClickListener {
            changeToActivityAddTeam()
        }
    }

    private fun fillBlankSpaces() {
        val idTeam = intent.getStringExtra(ID_TEAM_NT)
        val idCup = intent.getStringExtra(ID_CUP_NT)
        if (idTeam != null && idCup == null) {
            lifecycleScope.launch {
                val teamName = dbAccess.teamDao().getTeamById(idTeam).name
                binding.newTeamName.setText(teamName)
            }
        } else if (idTeam == null && idCup != null) {
            lifecycleScope.launch {
                val teamName = "Team ${dbAccess.teamDao().getTeamsByCupId(idCup).size + 1}"
                binding.newTeamName.setText(teamName)
            }
        }
    }

    private fun saveButton() {
        binding.saveButton.setOnClickListener {
            val idTeam = intent.getStringExtra(ID_TEAM_NT)
            val idCup = intent.getStringExtra(ID_CUP_NT)
            if (idTeam == null && idCup != null) {
                addTeam()
            }
            if (idTeam != null && idCup == null) {
                updateTeam()
            }
        }
    }

    private fun addTeam() {
        val idCup = intent.getStringExtra(ID_CUP_NT)
        if (idCup != null) {
            lifecycleScope.launch {
                val teamName = binding.newTeamName.text.toString().ifBlank {
                    val num = dbAccess.teamDao().getTeamsByCupId(idCup.toString()).size + 1
                    "Team $num"
                }
                val newTeam = Team(name = teamName, idCup = idCup.toInt())
                dbAccess.teamDao().insert(newTeam)
                changeToActivityAddTeam()
            }
        }
    }

    private fun updateTeam() {
        val idTeam = intent.getStringExtra(ID_TEAM_NT)
        if (idTeam != null) {
            lifecycleScope.launch {
                val team = dbAccess.teamDao().getTeamById(idTeam)
                val newTeam = Team(
                    id = team.id,
                    name = binding.newTeamName.text.toString(),
                    idCup = team.idCup
                )
                dbAccess.teamDao().update(newTeam)
                changeToActivityAddTeam()
            }
        }
    }

    private fun changeToActivityAddTeam() {
        val activityAddTeam = Intent(this, ActivityAddTeam::class.java)
        val idCup = intent.getStringExtra(ID_CUP_NT)
        val idTeam = intent.getStringExtra(ID_TEAM_NT)
        if (idCup != null) {
            activityAddTeam.putExtra(ID_CUP_AT, idCup)
            startActivity(activityAddTeam)
        } else if (idTeam != null) {
            lifecycleScope.launch {
                val team = dbAccess.teamDao().getTeamById(idTeam)
                val newIdCup = team.idCup.toString()
                activityAddTeam.putExtra(ID_CUP_AT, newIdCup)
                startActivity(activityAddTeam)
            }
        }
    }
}