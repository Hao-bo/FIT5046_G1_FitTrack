package com.example.fittrackapp

data class Exercise(
    val id: Int,
    val name: String?,
    val description: String?
)

data class ExerciseResponse(
    val results: List<Exercise>
)
