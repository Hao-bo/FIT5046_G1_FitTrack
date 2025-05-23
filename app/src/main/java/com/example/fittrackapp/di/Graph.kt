package com.example.fittrackapp.di

import android.content.Context
import androidx.room.Room
import com.example.fittrackapp.data.local.WorkoutDatabase
import com.example.fittrackapp.data.repository.ExerciseRepository
import com.example.fittrackapp.data.repository.UserRepository
import com.example.fittrackapp.data.repository.WorkoutRepository
import com.example.fittrackapp.ui.auth.AuthViewModel
import com.example.fittrackapp.ui.form.FormViewModel

/**
 * A singleton object acting as a simple dependency injection container.
 * It provides instances of ViewModels, Repositories, and the Database.
 * This pattern is often used in smaller Android applications to manage dependencies
 * without a full-fledged DI framework like Hilt or Koin.
 */
object Graph {

    lateinit var authViewModel: AuthViewModel
    lateinit var database: WorkoutDatabase

    // Lazily initialized FormViewModel.
    // It depends on workoutRepository and authViewModel, which must be initialized before this is accessed.
    val formViewModel by lazy {
        FormViewModel(workoutRepository = workoutRepository, authViewModel = authViewModel)
    }

    // Lazily initialized WorkoutRepository.
    // It depends on the WorkoutDao obtained from the database.
    val workoutRepository by lazy {
        WorkoutRepository(workoutDao = database.workoutDao())
    }


    // Lazily initialized ExerciseRepository.
    // This repository seems to be self-contained or its dependencies are handled internally.
    val exerciseRepository by lazy {
        ExerciseRepository()
    }


    // Lazily initialized UserRepository.
    // Similar to ExerciseRepository, it appears self-contained or manages its own dependencies.
    val userRepository by lazy {
        UserRepository()
    }



    /**
     * Initializes the database and the AuthViewModel.
     * This function should be called once, typically in the Application's onCreate() method,
     * to set up the core dependencies.
     *
     * @param context The application context, used for initializing the Room database.
     * @param authVM The instance of AuthViewModel to be used by the Graph.
     * It's passed in, suggesting it might be created with Application context elsewhere.
     */
    fun provide(context: Context, authVM: AuthViewModel) {
        // Build the Room database instance.
        // "workout.db" is the name of the database file.
        // fallbackToDestructiveMigration(false) means if migrations are not provided and schema changes,
        // the app will crash rather than losing data. Consider true for development or add proper migrations.
        database = Room.databaseBuilder(context, WorkoutDatabase::class.java, "workout.db")
            .fallbackToDestructiveMigration(false)
            .build()
        // Assign the provided AuthViewModel instance.
        authViewModel = authVM
    }
}