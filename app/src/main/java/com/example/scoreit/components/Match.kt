package com.example.scoreit.components

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Cup::class,
            parentColumns = ["id"],
            childColumns = ["idCup"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Team::class,
            parentColumns = ["id"],
            childColumns = ["idFirstTeam"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Team::class,
            parentColumns = ["id"],
            childColumns = ["idSecondTeam"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Match(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val stage: Int? = null,
    var playable: Boolean = true,
    val matchDay: Int? = null,
    val firstOfKind: Boolean = false,
    var idFirstTeam: Int,
    var idSecondTeam: Int,
    var firstTeamPoints: Int = 0,
    var secondTeamPoints: Int = 0,
    var firstTeamRounds: Int? = null,
    var secondTeamRounds: Int? = null,
    val firstMatch: Boolean = true,
    var idWinner: Int? = null,
    val idCup: Int
) : Serializable
