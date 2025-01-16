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

data class Team(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var logo: String? = null,
    var name: String,
    var finalPoints: Int = 0,
    var pointsWon: Int = 0,
    var pointsLost: Int = 0,
    var matchesPlayed: Int = 0,
    var matchesWon: Int = 0,
    var matchesLost: Int = 0,
    var roundsWon: Int? = null,
    var roundsLost: Int? = null,
    var playersList: MutableList<String?> = mutableListOf(),
    val idCup: Int
): Serializable
