package com.example.scoreit.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.scoreit.model.components.Cup

@Dao
interface CupDao {
    @Query("SELECT * FROM Cup")
    suspend fun getEveryCup(): MutableList<Cup>

    @Query("SELECT * FROM Cup WHERE id =:id")
    suspend fun getCupById(id: String): Cup

    @Query("SELECT * FROM Cup WHERE idUser =:idUser")
    suspend fun getCupsByUserId(idUser: String): List<Cup>

    @Update
    suspend fun update(cup: Cup)

    @Insert
    suspend fun insert(cup: Cup): Long

    @Insert
    suspend fun insertListOfCups(cups: MutableList<Cup>)

    @Query("DELETE FROM Cup WHERE id =:id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM Cup WHERE idUser =:idUser")
    suspend fun deleteCupsByIdUser(idUser: String)
}