package com.example.scoreit.database

import androidx.room.TypeConverter
import com.example.scoreit.components.Cup
import com.example.scoreit.components.Team
import com.example.scoreit.components.Match
import com.example.scoreit.components.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromTeam(team: Team): String {
        return gson.toJson(team)
    }

    @TypeConverter
    fun toTeam(json: String): Team {
        return try {
            gson.fromJson(json, Team::class.java)
        } catch (e: Exception) {
            Team(
                id = -1,
                name = "Default Team",
                idCup = -1,
                finalPoints = "0",
                ingamePoints = "0",
                matchesPlayed = "0",
                roundsInFavor = "0",
                roundsAgainst = "0",
                matchesWon = "0"
            )
        }
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
    fun fromEquipoList(teams: List<Team>): String {
        return gson.toJson(teams)
    }

    @TypeConverter
    fun toEquipoList(json: String): List<Team> {
        return try {
            val listType = object : TypeToken<List<Team>>() {}.type
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // User
    @TypeConverter
    fun fromUser(user: User?): String? = gson.toJson(user)

    @TypeConverter
    fun toUser(usuarioString: String?): User? =
        usuarioString?.let { gson.fromJson(it, User::class.java) }

    @TypeConverter
    fun fromList(value: List<String>?): String? = value?.joinToString(",")

    @TypeConverter
    fun toList(value: String?): List<String> = value?.split(",") ?: emptyList()

    @TypeConverter
    fun fromIntList(value: List<Int>?): String? = value?.joinToString(",")

    @TypeConverter
    fun toIntList(value: String?): List<Int> = value?.split(",")?.map { it.toInt() } ?: emptyList()

    @TypeConverter
    fun fromBoolean(value: Boolean): Int = if (value) 1 else 0

    @TypeConverter
    fun toBoolean(value: Int): Boolean = value == 1


    @TypeConverter
    fun fromBoolean(value: Boolean?): Int? = value?.let { if (it) 1 else 0 }

    @TypeConverter
    fun toBoolean(value: Int?): Boolean? = value?.let { it == 1 }

}
