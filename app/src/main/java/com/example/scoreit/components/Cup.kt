package com.example.scoreit.components

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["idUser"],
        onDelete = ForeignKey.CASCADE)
    ]
)

data class Cup(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var hasStarted: Boolean = false,
    var name: String,
    var startDate: String,
    var requiredPoints: Int?,
    var requiredRounds: Int?,
    var doubleMatch: Boolean,
    var alwaysWinner: Boolean,
    var twoPointsDifference: Boolean,
    var playableMatches: Int = 1,
    var winner: String?,
    val idUser: Int
)
