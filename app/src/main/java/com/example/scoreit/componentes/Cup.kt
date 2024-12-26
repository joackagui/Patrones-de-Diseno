package com.example.scoreit.componentes

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
    //var pointsLimit: Boolean,
    var winningPoints: Int?,
    //var timeLimit: Boolean,
    var finishTime: Int?,
    var gameMode: String,
    //var canPause: Boolean,
    var restingTime: Int?,
    var restingAmount: Int?,
    //var roundsToWin: Boolean,
    var roundsAmount: Int?,
    var twoMatches: Boolean,
    var alwaysWinner: Boolean,
    var twoPointsDifference: Boolean,
    val idUser: Int
    //var twoRoundsDifference: Boolean?
)
