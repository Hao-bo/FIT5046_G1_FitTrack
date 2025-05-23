package com.example.fittrackapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fittrackapp.data.model.WorkoutSession


@Database(
    entities = [WorkoutSession::class],
    version = 2,
    exportSchema = false
)

abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}