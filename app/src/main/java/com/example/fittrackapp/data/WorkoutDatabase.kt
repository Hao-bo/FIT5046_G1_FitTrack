package com.example.fittrackapp.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [WorkoutSession::class],
    version = 2,
    exportSchema = false
)

abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}