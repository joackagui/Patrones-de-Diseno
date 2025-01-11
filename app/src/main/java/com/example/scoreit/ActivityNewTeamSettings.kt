package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.ActivityAddTeam.Companion.ID_CUP_AT
import com.example.scoreit.ActivityChangeUserData.Companion.ID_USER_CUD
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

        setHeader()
        setData()
        backButton()
        saveButton()
        deleteButton()
    }

    private fun AppCompatActivity.setHeader() {
        val userButton = findViewById<ImageView>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)
        val idTeam = intent.getStringExtra(ID_TEAM_NT)
        val idCup = intent.getStringExtra(ID_CUP_NT)

        userButton.setOnClickListener {
            if(idTeam == null && idCup != null){
                lifecycleScope.launch {
                    val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    deleteCup(idCup)
                    changeToActivityChangeUserData(idUser)
                }

            } else if(idTeam != null && idCup == null){
                lifecycleScope.launch {
                    val newIdCup = dbAccess.teamDao().getTeamById(idTeam).idCup.toString()
                    val idUser = dbAccess.cupDao().getCupById(newIdCup).idUser.toString()
                    deleteCup(newIdCup)
                    changeToActivityChangeUserData(idUser)
                }
            }
        }

        scoreItCup.setOnClickListener {
            if(idTeam == null && idCup != null){
                lifecycleScope.launch {
                    val idUser = dbAccess.cupDao().getCupById(idCup).idUser.toString()
                    deleteCup(idCup)
                    changeToActivityMainMenu(idUser)
                }

            } else if(idTeam != null && idCup == null){
                lifecycleScope.launch {
                    val newIdCup = dbAccess.teamDao().getTeamById(idTeam).idCup.toString()
                    val idUser = dbAccess.cupDao().getCupById(newIdCup).idUser.toString()
                    deleteCup(newIdCup)
                    changeToActivityMainMenu(idUser)
                }
            }
        }
    }

    private fun backButton() {
        binding.backButton.setOnClickListener {
            val idCup = intent.getStringExtra(ID_CUP_NT)
            val idTeam = intent.getStringExtra(ID_TEAM_NT)
            if (idCup != null) {
                changeToActivityAddTeam(idCup)
            } else if (idTeam != null) {
                lifecycleScope.launch {
                    val team = dbAccess.teamDao().getTeamById(idTeam)
                    val newIdCup = team.idCup.toString()
                    changeToActivityAddTeam(newIdCup)
                }
            }
        }
    }

    private fun deleteButton() {
        val idTeam = intent.getStringExtra(ID_TEAM_NT)
        val idCup = intent.getStringExtra(ID_CUP_NT)
        binding.deleteButton.setOnClickListener {
            if (idTeam != null && idCup == null) {
                deleteTeam(idTeam)
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

    private fun deleteTeam(idTeam: String){
        lifecycleScope.launch {
            val actualIdCup = dbAccess.teamDao().getTeamById(idTeam).idCup.toString()
            dbAccess.teamDao().deleteById(idTeam)
            changeToActivityAddTeam(actualIdCup)
        }
    }

    private fun deleteCup(idCup: String) {
        lifecycleScope.launch {
            dbAccess.cupDao().deleteById(idCup)
        }
    }

    private fun setData() {
        val idTeam = intent.getStringExtra(ID_TEAM_NT)
        val idCup = intent.getStringExtra(ID_CUP_NT)
        if (idTeam != null && idCup == null) {
            lifecycleScope.launch {
                val teamName = dbAccess.teamDao().getTeamById(idTeam).name
                binding.newTeamName.setText(teamName)
                binding.deleteButton.visibility = View.VISIBLE
            }
        } else if (idTeam == null && idCup != null) {
            lifecycleScope.launch {
                val teamName = "Team ${dbAccess.teamDao().getTeamsByCupId(idCup).size + 1}"
                binding.newTeamName.setText(teamName)
                binding.deleteButton.visibility = View.GONE
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
                changeToActivityAddTeam(idCup.toString())
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
                changeToActivityAddTeam(team.idCup.toString())
            }
        }
    }

    private fun changeToActivityAddTeam(idCup: String) {
        val activityAddTeam = Intent(this, ActivityAddTeam::class.java)
        activityAddTeam.putExtra(ID_CUP_AT, idCup)

        startActivity(activityAddTeam)
        finish()
    }

    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ID_USER_MM, idUser)
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish()
    }

    private fun changeToActivityChangeUserData(idUser: String) {
        val activityChangeUserData = Intent(this, ActivityChangeUserData::class.java)
        activityChangeUserData.putExtra(ID_USER_CUD, idUser)

        startActivity(activityChangeUserData)
        finish()
    }
}