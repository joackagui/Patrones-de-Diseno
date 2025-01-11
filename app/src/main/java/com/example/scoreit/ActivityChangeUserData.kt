package com.example.scoreit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.scoreit.ActivityMainMenu.Companion.ID_USER_MM
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivityChangeUserDataBinding
import kotlinx.coroutines.launch


class ActivityChangeUserData : AppCompatActivity() {
    private lateinit var binding: ActivityChangeUserDataBinding
    private lateinit var dbAccess: AppDataBase

    companion object {
        const val ID_USER_CUD: String = "USER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbAccess = getDatabase(this)

        setData()

        changeUserButton()
        deleteButton()
        saveButton()
    }

    private fun changeUserButton() {
        binding.changeUserButton.setOnClickListener {
            changeToActivitySelectUser()
        }
    }

    private fun deleteButton() {
        binding.deleteButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_CUD)
            if (idUser != null) {
                lifecycleScope.launch {
                    val user = dbAccess.userDao().getUserById(idUser)
                    user.name = "user ${user.id}"
                    user.email = null
                    user.password = null
                    dbAccess.userDao().update(user)
                    deleteEverythingOfUser(idUser)
                    changeToActivitySelectUser()
                }
            }
        }
    }

    private fun saveButton() {
        binding.saveButton.setOnClickListener {
            val idUser = intent.getStringExtra(ID_USER_CUD)
            if (idUser != null) {
                lifecycleScope.launch {
                    val user = dbAccess.userDao().getUserById(idUser)
                    user.name = binding.userName.text.toString()
                    user.email = binding.userEmail.text.toString()
                    user.password = binding.userPassword.text.toString()
                    dbAccess.userDao().update(user)
                    changeToActivityMainMenu()
                }
            }
        }
    }

    private fun setData() {
        val idUser = intent.getStringExtra(ID_USER_CUD)
        if (idUser != null) {
            lifecycleScope.launch {
                val user = dbAccess.userDao().getUserById(idUser)
                binding.userName.setText(user.name)
                binding.userEmail.setText(user.email)
                binding.userPassword.setText(user.password)
            }
        }
    }

    private fun deleteEverythingOfUser(idUser: String) {
        lifecycleScope.launch {
            dbAccess.cupDao().deleteCupsByIdUser(idUser)
        }
    }

    private fun changeToActivityMainMenu() {
        val idUser = intent.getStringExtra(ID_USER_CUD)
        if (idUser != null) {
            val activityMainMenu = Intent(this, ActivityMainMenu::class.java)
            activityMainMenu.putExtra(ID_USER_MM, idUser)
            activityMainMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(activityMainMenu)
            finish()
        }
    }

    private fun changeToActivitySelectUser() {
        val activitySelectUser = Intent(this, ActivitySelectUser::class.java)
        activitySelectUser.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(activitySelectUser)
        finish()
    }
}