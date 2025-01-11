package com.example.scoreit.components

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.io.Serializable

@Entity(
    foreignKeys = [ForeignKey(
        entity = Cup::class,
        parentColumns = ["id"],
        childColumns = ["idCup"],
        onDelete = ForeignKey.CASCADE)
    ]
)

data class Match(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val stage: Int? = null,
    val playable: Boolean = true,
    val matchDay: Int? = null,
    val firstOfKind: Boolean = false,
    val firstTeamJson: String,
    val secondTeamJson: String,
    var firstTeamPoints: Int = 0,
    var secondTeamPoints: Int = 0,
    var firstTeamRounds: Int? = null,
    var secondTeamRounds: Int? = null,
    val inGamePointsPerRoundList: MutableList<String?> = mutableListOf(),
    val idCup: Int
): Serializable
