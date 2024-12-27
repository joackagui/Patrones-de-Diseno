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
    var id: Int = 0,
    var logo: String? = null,
    var selected: Boolean = false,
    var name: String,
    var startDate: String,
    var winningPoints: Int?,
    var finishTime: Int?,
    var gameMode: String,
    var restingTime: Int?,
    var restingAmount: Int?,
    var roundsAmount: Int?,
    var doubleMatch: Boolean,
    var alwaysWinner: Boolean,
    var twoPointsDifference: Boolean,
    val idUser: Int
)
