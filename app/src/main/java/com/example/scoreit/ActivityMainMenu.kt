package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreit.ActivityNewCupSettings.Companion.ID_USER_NC
import com.example.scoreit.adapters.RecyclerCups
import com.example.scoreit.databinding.ActivityMainMenuBinding
import com.example.scoreit.componentes.User
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
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
        val view = binding.root
        setContentView(view)

        dbAccess = getDatabase(this)

        setHeader()
        setText()
        setUpRecyclerView()
        newCupButton()
    }

    private fun AppCompatActivity.setHeader() {
//        val userButton = findViewById<Button>(R.id.user_button)
//
//        userButton.setOnClickListener {
//            val intent = Intent(this, ActivityLogIn::class.java)
//            startActivity(intent)
//        }

    }

    private fun newCupButton() {
        val idUser = intent.getStringExtra(ID_USER_MM)
        if (idUser != null) {
            lifecycleScope.launch {
                val user: User = dbAccess.userDao().getUserById(idUser.toString())
                changeToNewCupSettings(user)
            }
        }
    }

    private fun changeToNewCupSettings(user: User) {
        binding.createNewCup.setOnClickListener {
            val activityNewCupSettings = Intent(this, ActivityNewCupSettings::class.java)
            activityNewCupSettings.putExtra(ID_USER_NC, user.id)
            startActivity(activityNewCupSettings)
        }
    }

    private fun setUpRecyclerView() {
        val idUser = intent.getStringExtra(ID_USER_MM)
        if (idUser != null) {
            lifecycleScope.launch {
                val cupsCreatedByThisUser =
                    dbAccess.cupDao().getCupsByUserId(idUser).toMutableList()
                recyclerCups.addDataToList(cupsCreatedByThisUser)

                binding.recyclerCups.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = recyclerCups
                }
            }
        }
    }

    private fun setText() {
        val idUser = intent.getStringExtra(ID_USER_MM)
        if (idUser != null) {
            lifecycleScope.launch {
                val userCup: TextView = binding.createdCupsText
                val userName = dbAccess.userDao().getUserById(idUser.toString()).name
                val finalText = "${userName}'s cups"
                userCup.text = finalText
            }
        }
    }
}
