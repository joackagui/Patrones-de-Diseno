package com.example.scoreit.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.scoreit.model.components.User

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    suspend fun getEveryUser(): List<User>

    @Query("SELECT * FROM User WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun getUserById(id: String): User

    @Query("SELECT * FROM User WHERE lastUser = 1")
    suspend fun getLastUser(): User

    @Update
    suspend fun update(user: User)

    @Insert
    suspend fun insert(user: User)
}