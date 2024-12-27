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

    @Query(
        """
        SELECT c.roundsAmount
        FROM `Match` p
        INNER JOIN Cup c ON p.idCup = c.id
        WHERE p.id = :idPartido
    """
    )
    suspend fun obtenerSiHayRondas(idPartido: String): Boolean

//    @Query(
//        """
//        SELECT c.diferenciaDosPuntos
//        FROM `Match` p
//        INNER JOIN Cup c ON p.idCampeonato = c.id
//        WHERE p.id = :idPartido
//    """
//    )
//    suspend fun obtenerSiHayDiferenciaDeDosPuntos(idPartido: String): Boolean
//
//    @Query(
//        """
//        SELECT c.seJuegaPorPuntosMaximos
//        FROM `Match` p
//        INNER JOIN Cup c ON p.idCampeonato = c.id
//        WHERE p.id = :idPartido
//    """
//    )
//    suspend fun obtenerSiPartidoPorPuntos(idPartido: String): Boolean
//
//    @Query(
//        """
//        SELECT c.seJuegaPorTiempoMaximo
//        FROM `Match` p
//        INNER JOIN Cup c ON p.idCampeonato = c.id
//        WHERE p.id = :idPartido
//    """
//    )
//    suspend fun obtenerSiPartidoPorTiempo(idPartido: String): Boolean
//
//    @Query(
//        """
//        SELECT c.tiempoDeJuego
//        FROM `Match` p
//        INNER JOIN Cup c ON p.idCampeonato = c.id
//        WHERE p.id = :idPartido
//    """
//    )
//    suspend fun obtenerTiempoDelPartido(idPartido: String): Int
//
//    @Query(
//        """
//        SELECT c.puntosParaGanar
//        FROM `Match` p
//        INNER JOIN Cup c ON p.idCampeonato = c.id
//        WHERE p.id = :idPartido
//    """
//    )
//    suspend fun obtenerPuntosParaGanar(idPartido: String): Int
//
//    @Query(
//        """
//        SELECT c.siempreUnGanador
//        FROM `Match` p
//        INNER JOIN Cup c ON p.idCampeonato = c.id
//        WHERE p.id = :idPartido
//    """
//    )
//    suspend fun obtenerSiempreUnGanador(idPartido: String): Boolean

    @Update
    suspend fun update(match: Match)

    @Insert
    suspend fun insert(match: Match)

    @Insert
    suspend fun insertMatches(match: MutableList<Match>)
}