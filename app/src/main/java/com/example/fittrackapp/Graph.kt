package com.example.fittrackapp

import android.content.Context
import androidx.room.Room
import com.example.fittrackapp.data.WorkoutDatabase
import com.example.fittrackapp.data.WorkoutRepository

object Graph {
    lateinit var database: WorkoutDatabase

    val workoutRepository by lazy {
        WorkoutRepository(workoutDao = database.workoutDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, WorkoutDatabase::class.java, "workout.db").build()
    }
}