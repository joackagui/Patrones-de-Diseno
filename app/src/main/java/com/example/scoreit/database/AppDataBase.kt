package com.example.scoreit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.scoreit.components.User
import com.example.scoreit.components.Cup
import com.example.scoreit.components.Match
import com.example.scoreit.components.Team

@Database(
    entities = [User::class, Cup::class, Match::class, Team::class],
    version = 6,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cupDao(): CupDao
    abstract fun matchDao(): MatchDao
    abstract fun teamDao(): TeamDao

    companion object{
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this){
                val room = Room
                    .databaseBuilder(
                        context,
                        AppDataBase::class.java,
                        "Score_it_database"
                    ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = room
                room
            }
        }
    }
}
