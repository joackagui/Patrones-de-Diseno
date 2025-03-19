package com.example.scoreit.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.components.User
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
    }

    private fun loginButton() {
        lifecycleScope.launch {
            binding.logInButton.setOnClickListener {
                if (binding.emailLogIn.text.toString() == "" || binding.passwordLogIn.text.toString() == "") {
                    errorMessage(1)
                } else if (binding.passwordLogIn.text.toString().length < 8) {
                    errorMessage(2)
                } else {
                    logInVerification()
                }
            }
        }
    }

    private fun logInVerification() {
        val idUser = intent.getStringExtra(ID_USER_LI)
        if (idUser != null) {
            lifecycleScope.launch {
                val user = dbAccess.userDao().getUserById(idUser)
                val email = binding.emailLogIn.text.toString()
                val password = binding.passwordLogIn.text.toString()

                if (user.email != email || user.password != password) {
                    errorMessage(4)
                } else {
                    successfulMessage(user)
                    lastUserUpdate()
                    changeToActivityMainMenu(idUser)
                }
            }
        }
    }

    private fun successfulMessage(user: User) {
        Toast.makeText(this, "Welcome ${user.name}", Toast.LENGTH_LONG).show()
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

    private fun errorMessage(number: Int) {
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
        }
    }

    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this@ActivityLogIn, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ActivityMainMenu.Companion.ID_USER_MM, idUser)

        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish()
    }
}
