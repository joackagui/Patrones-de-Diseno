package com.example.scoreit.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreit.R
import com.example.scoreit.recyclers.RecyclerCups
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityMainMenuBinding
import kotlinx.coroutines.launch

class ActivityMainMenu : AppCompatActivity() {

    private val recyclerCups: RecyclerCups by lazy { RecyclerCups() }
    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_USER_MM: String = "USER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbAccess = getDatabase(this)

        setHeader()
        setText()
        setUpRecyclerView()

        newCupButton()
    }

    private fun AppCompatActivity.setHeader() {
        val userButton = findViewById<ImageView>(R.id.user_button)
        val scoreItCup = findViewById<ImageView>(R.id.score_it_cup)

        userButton.setOnClickListener {
            changeToActivityChangeUserData()
        }

        scoreItCup.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_MM)
            if(idUser != null) {
                changeToActivityMainMenu(idUser)
            }
        }
    }

    private fun newCupButton() {
        val idUser = intent.getStringExtra(ID_USER_MM)
        if (idUser != null) {
            changeToNewCupSettings(idUser)
        }
    }

    private fun setText() {
        val idUser = intent.getStringExtra(ID_USER_MM)
        if (idUser != null) {
            lifecycleScope.launch {
                val userCup: TextView = binding.createdCupsText
                val userName = dbAccess.userDao().getUserById(idUser).name
                val finalText = "${userName}'s cups"
                userCup.text = finalText
            }
        }
    }

    private fun setUpRecyclerView() {
        val idUser = intent.getStringExtra(ID_USER_MM)
        if (idUser != null) {
            lifecycleScope.launch {
                val listOfCups = dbAccess.cupDao().getCupsByUserId(idUser).toMutableList()
                recyclerCups.addDataToList(listOfCups)

                binding.recyclerCups.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = recyclerCups
                }
            }
        }
    }

    private fun changeToNewCupSettings(idUser: String) {
        binding.createNewCup.setOnClickListener {
            val activityNewCupSettings = Intent(this, ActivityNewCupSettings::class.java)
            activityNewCupSettings.putExtra(ActivityNewCupSettings.Companion.ID_USER_NC, idUser)

            startActivity(activityNewCupSettings)
        }
    }

    private fun changeToActivityChangeUserData() {
        val activityChangeUserData = Intent(this, ActivityChangeUserData::class.java)
        activityChangeUserData.putExtra(ActivityChangeUserData.Companion.ID_USER_CUD, intent.getStringExtra(ID_USER_MM))

        startActivity(activityChangeUserData)
    }

    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ID_USER_MM, idUser)
        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish()
    }
}
