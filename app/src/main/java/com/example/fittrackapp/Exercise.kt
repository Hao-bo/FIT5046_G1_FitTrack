package com.example.fittrackapp

// For a single course object
data class Exercise(
    val id: Int,
    val name: String,
    val description: String
)

// Used to receive the entire API response (containing a results list)
data class ExerciseResponse(
    val results: List<Exercise>
)
