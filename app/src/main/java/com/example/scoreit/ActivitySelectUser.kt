package com.example.scoreit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scoreit.adapters.RecyclerUsers
import com.example.scoreit.components.User
import com.example.scoreit.database.AppDataBase
import com.example.scoreit.database.AppDataBase.Companion.getDatabase
import com.example.scoreit.databinding.ActivitySelectUserBinding
import kotlinx.coroutines.launch

class ActivitySelectUser : AppCompatActivity() {

    private val recyclerUsers: RecyclerUsers by lazy { RecyclerUsers() }
    private lateinit var binding: ActivitySelectUserBinding
    private lateinit var dbAccess: AppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        dbAccess = getDatabase(this)

        createAccounts()
        //setUpRecyclerView()
    }

    private fun createAccounts() {
        lifecycleScope.launch {
            if(dbAccess.userDao().getEveryUser().isEmpty()){
                for (i in 1..5) {
                    val nuevoUser = User(name = "User $i")
                    dbAccess.userDao().insert(nuevoUser)
                }
            }
            setUpRecyclerView()
        }
    }

    private fun setUpRecyclerView() {
        lifecycleScope.launch {
            val userList = dbAccess.userDao().getEveryUser().toMutableList()
            recyclerUsers.addDataToList(userList)

            binding.recyclerUsers.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = recyclerUsers

            }
        }
    }
}