package com.example.armenianairlines.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.armenianairlines.data.model.Flight

@Database(entities = [Flight::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun flightDao(): FlightDao

    companion object {
        private const val DB_NAME = "airline.db"
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(application: Application): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    application,
                    AppDatabase::class.java,
                    DB_NAME
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}