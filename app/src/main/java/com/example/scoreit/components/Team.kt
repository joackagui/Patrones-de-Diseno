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
    val logo: String? = null,
    val name: String,
    var finalPoints: String = "0",
    var ingamePoints: String = "0",
    var matchesPlayed: String = "0",
    var matchesWon: String = "0",
    var matchesLost: String = "0",
    var roundsInFavor: String? = "0",
    var roundsAgainst: String? = "0",
    val idCup: Int
): Serializable
