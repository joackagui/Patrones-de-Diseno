package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.ActivityMainMenu.Companion.ID_USER_MM
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityLogInBinding
import kotlinx.coroutines.launch

class ActivityLogIn : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_USER_LI: String = "ID_USER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        dbAccess = getDatabase(this)

        loginButton()
        changeActivityToSignUp()
    }

    private fun changeActivityToSignUp() {
        binding.newUserButton.setOnClickListener {
            val activitySignUp = Intent(this, ActivitySignUp::class.java)
            startActivity(activitySignUp)
        }
    }

    private fun changeToActivityMainMenu() {
        val userId = intent.getStringExtra(ID_USER_LI)
        if (userId != null) {
            lifecycleScope.launch {
                val user = dbAccess.userDao().getUserById(userId)
                val activityMainMenu = Intent(this@ActivityLogIn, ActivityMainMenu::class.java)
                activityMainMenu.putExtra(ID_USER_MM, user.id.toString())
                startActivity(activityMainMenu)
            }
        }
    }

    private fun loginButton() {
        lifecycleScope.launch {
            binding.logInButton.setOnClickListener {
                if (binding.emailLogIn.text.toString() == "" || binding.passwordLogIn.text.toString() == "") {
                    message(1)
                } else if (binding.passwordLogIn.text.toString().length < 8) {
                    message(2)
                } else {
                    logInVerification()
                }
            }
        }
    }

    private fun logInVerification() {
        val userId = intent.getStringExtra(ID_USER_LI)
        if (userId != null) {
            lifecycleScope.launch {
                val user = dbAccess.userDao().getUserById(userId)
                val email = binding.emailLogIn.text.toString()
                val password = binding.passwordLogIn.text.toString()

                if (user.email != email || user.password != password) {
                    message(4)
                } else {
                    message(5)
                    lastUserUpdate()
                    changeToActivityMainMenu()
                }
            }
        }
    }

    private fun lastUserUpdate() {
        lifecycleScope.launch {
            val listOfUsers = dbAccess.userDao().getEveryUser().toMutableList()
            for (user in listOfUsers) {
                if (user.lastUser) {
                    user.lastUser = false
                    dbAccess.userDao().update(user)
                }
            }
            val idUser = intent.getStringExtra(ID_USER_LI)
            if (idUser != null) {
                val user = dbAccess.userDao().getUserById(idUser)
                user.lastUser = true
                dbAccess.userDao().update(user)
            }
        }
    }

    private fun message(number: Int) {
        when (number) {
            1 -> {
                Toast.makeText(this, "You must fill in all fields", Toast.LENGTH_LONG).show()
            }

            2 -> {
                Toast.makeText(this, "Password too short", Toast.LENGTH_LONG).show()
            }

            3 -> {
                Toast.makeText(this, "User does not exist", Toast.LENGTH_LONG).show()
            }

            4 -> {
                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_LONG).show()
            }

            5 -> {
                Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show()
            }
        }
    }
}
