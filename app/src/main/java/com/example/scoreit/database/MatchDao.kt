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

    @Query(
        """
        SELECT c.restingAmount
        FROM `Match` m
        INNER JOIN Cup c ON m.idCup = c.id
        WHERE m.id = :idMatch
    """
    )
    suspend fun getRestingAmount(idMatch: String): Int

    @Query(
        """
        SELECT c.doubleMatch
        FROM `Match` m
        INNER JOIN Cup c ON m.idCup = c.id
        WHERE m.id = :idMatch
    """
    )
    suspend fun getIfTwoMatches(idMatch: String): Boolean

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