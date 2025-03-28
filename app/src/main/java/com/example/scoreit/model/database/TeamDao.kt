package com.example.scoreit.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.scoreit.model.components.Team

@Dao
interface TeamDao {
    @Query("SELECT * FROM Team")
    suspend fun getEveryTeam(): List<Team>

    @Query("SELECT * FROM Team WHERE id =:id")
    suspend fun getTeamById(id: String): Team

    @Query("SELECT * FROM Team WHERE idCup =:idCup")
    suspend fun getTeamsByCupId(idCup: String): List<Team>

    @Update
    suspend fun update(team: Team)

    @Insert
    suspend fun insert(team: Team)

    @Insert
    suspend fun insertListOfTeams(team: List<Team>)

    @Query("DELETE FROM Team WHERE id =:id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM Team WHERE idCup =:idCup")
    suspend fun deleteTeamsByIdCup(idCup: String)
}