package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.ActivityAddTeam.Companion.ID_CUP_AT
import com.example.scoreit.ActivityMainMenu.Companion.ID_USER_MM
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

        fillBlankSpaces()
        setHeader()
        backButton()
        saveButton()

    }

    private fun backButton() {
        binding.backButton.setOnClickListener {
            changeToActivityAddTeam()
        }
    }

    private fun fillBlankSpaces() {
        val idTeam = intent.getStringExtra(ID_TEAM_NT)
        if (idTeam != null) {
            lifecycleScope.launch {
                val team = dbAccess.teamDao().getTeamById(idTeam)
                binding.newTeamName.hint = team.name
            }
        }
    }

    private fun AppCompatActivity.setHeader() {
//        val userButton = findViewById<Button>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)
//
//        userButton.setOnClickListener {
//            val intent = Intent(this, ActivityLogIn::class.java)
//            startActivity(intent)
//        }
//
        scoreItCup.setOnClickListener {
            val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
            val idCup = intent.getStringExtra(ID_CUP_NT)
            var idUser = ""
            lifecycleScope.launch {
                idUser = dbAccess.cupDao().getCupById(idCup.toString()).idUser.toString()
            }
            activityMainMenu.putExtra(ID_USER_MM, idUser)
            startActivity(activityMainMenu)
        }
    }

    private fun saveButton() {
        binding.saveButton.setOnClickListener {
            val idTeam = intent.getStringExtra(ID_TEAM_NT)
            if (idTeam == null) {
                addTeam()
            } else {
                updateTeam(idTeam)
            }
            changeToActivityAddTeam()
        }
    }

    private fun changeToActivityAddTeam() {
        val activityAddTeam = Intent(this, ActivityAddTeam::class.java)
        val idCup = intent.getStringExtra(ID_CUP_NT)
        activityAddTeam.putExtra(ID_CUP_AT, idCup)
        if (idCup != null) {
            startActivity(activityAddTeam)
        }
    }

    private fun addTeam() {
        val idCup = intent.getStringExtra(ID_CUP_AT)
        if (idCup != null) {
            lifecycleScope.launch {
                if (binding.newTeamName.text.toString() != "") {
                    val num = dbAccess.teamDao().getTeamsByCupId(idCup).size
                    val newTeam = Team(name = "Team $num", idCup = idCup.toInt())
                    dbAccess.teamDao().insert(newTeam)
                } else {
                    val newTeam =
                        Team(name = binding.newTeamName.text.toString(), idCup = idCup.toInt())
                    dbAccess.teamDao().insert(newTeam)
                }
            }
        }
    }

    private fun updateTeam(idTeam: String) {
        lifecycleScope.launch {
            val idCup = dbAccess.teamDao().getTeamById(idTeam).idCup.toString()
            val newTeam =
                Team(id = idTeam.toInt(), name = binding.newTeamName.text.toString(), idCup = idCup.toInt())
            dbAccess.teamDao().update(newTeam)
        }
    }
}