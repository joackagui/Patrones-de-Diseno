package com.example.scoreit.database

import androidx.room.TypeConverter
import com.example.scoreit.components.Cup
import com.example.scoreit.components.Team
import com.google.gson.Gson

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromTeam(team: Team): String {
        return gson.toJson(team)
    }

    @TypeConverter
    fun toTeam(json: String): Team {
        return gson.fromJson(json, Team::class.java)

    }

    @TypeConverter
    fun fromCup(cup: Cup): String {
        return gson.toJson(cup)
    }

    @TypeConverter
    fun toCup(json: String): Cup {
        return gson.fromJson(json, Cup::class.java)
    }

    @TypeConverter
    fun fromList(value: List<String>?): String? = value?.joinToString(",")

    @TypeConverter
    fun toList(value: String?): List<String> = value?.split(",") ?: emptyList()


}
