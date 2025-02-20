package com.example.scoreit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.scoreit.components.Match

@Dao
interface MatchDao {
    @Query("SELECT * FROM `Match`")
    suspend fun getEveryMatch(): List<Match>

    @Query("SELECT * FROM `Match` WHERE id =:id")
    suspend fun getMatchById(id: String): Match

    @Query("SELECT * FROM `Match` WHERE idCup =:idCup")
    suspend fun getMatchesByCupId(idCup: String): List<Match>

    @Query("SELECT * FROM `Match` WHERE matchDay =:matchDay")
    suspend fun getMatchesByMatchDay(matchDay: String): List<Match>

    @Update
    suspend fun update(match: Match)

    @Insert
    suspend fun insert(match: Match)

    @Insert
    suspend fun insertMatches(match: MutableList<Match>)

    @Query("DELETE FROM `Match` WHERE id =:id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM `Match` WHERE idCup =:idCup")
    suspend fun deleteMatchesByIdCup(idCup: String)

}