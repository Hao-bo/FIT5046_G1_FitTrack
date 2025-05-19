package com.example.fittrackapp

import android.content.Context
import androidx.room.Room
import com.example.fittrackapp.data.WorkoutDatabase
import com.example.fittrackapp.data.WorkoutRepository

object Graph {

    lateinit var authViewModel: AuthViewModel
    lateinit var database: WorkoutDatabase

    val formViewModel by lazy {
        FormViewModel(workoutRepository = workoutRepository, authViewModel = authViewModel)
    }

    val workoutRepository by lazy {
        WorkoutRepository(workoutDao = database.workoutDao())
    }

    fun provide(context: Context, authVM: AuthViewModel) {
        database = Room.databaseBuilder(context, WorkoutDatabase::class.java, "workout.db")
            .fallbackToDestructiveMigration(false)
            .build()
        authViewModel = authVM
    }
}