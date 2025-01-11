package com.example.scoreit.components

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var logo: String? = null,
    var email: String? = null,
    var password: String? = null,
    var lastUser: Boolean = false
)
