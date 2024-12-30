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
    val matchDay: Int? = null,
    val firstTeamJson: String,
    val secondTeamJson: String,
    var firstTeamPoints: Int = 0,
    var secondTeamPoints: Int = 0,
    var firstTeamRounds: String? = null,
    var secondTeamRounds: String? = null,
    val idCup: Int
): Serializable
