package com.example.scoreit.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.components.User
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivitySignUpBinding
import kotlinx.coroutines.launch

class ActivitySignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_USER_SU: String = "ID_USER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        dbAccess = getDatabase(this)

        signUpButton()
    }

    private fun signUpButton() {
        binding.createAccountButton.setOnClickListener {
            if (binding.usernameSignUp.text.toString() == "" || binding.emailSignUp.text.toString() == ""
                || binding.passwordSignUp.text.toString() == "" || binding.passwordSafetySignUp.text.toString() == ""
            ) {
                errorMessage(1)
            } else if (binding.passwordSignUp.text.toString() != binding.passwordSafetySignUp.text.toString()) {
                errorMessage(3)
            } else if (binding.passwordSignUp.text.toString().length < 8) {
                errorMessage(2)
            } else {
                noOneIsLastUser()
                saveUser()
            }
        }
    }

    private fun saveUser() {
        lifecycleScope.launch {
            val email = binding.emailSignUp.text.toString()
            val password = binding.passwordSignUp.text.toString()
            val name = binding.usernameSignUp.text.toString()
            val idUser = intent.getStringExtra(ID_USER_SU)
            if (idUser != null) {
                val newUser =
                    User(
                        email = email,
                        name = name,
                        password = password,
                        lastUser = true,
                        id = idUser.toInt()
                    )

                dbAccess.userDao().update(newUser)
                successfulMessage()
                changeToActivityMainMenu(idUser)
            }
        }
    }

    private fun noOneIsLastUser() {
        lifecycleScope.launch {
            val listOfUsers = dbAccess.userDao().getEveryUser().toMutableList()
            for (user in listOfUsers) {
                if (user.lastUser) {
                    user.lastUser = false
                    dbAccess.userDao().update(user)
                }
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
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun successfulMessage() {
        Toast.makeText(this, "User successfully created", Toast.LENGTH_LONG).show()
    }

    private fun changeToActivityMainMenu(idUser: String) {
        val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
        activityMainMenu.putExtra(ActivityMainMenu.Companion.ID_USER_MM, idUser)

        activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activityMainMenu)
        finish()
    }
}



