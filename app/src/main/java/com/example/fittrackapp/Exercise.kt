package com.example.fittrackapp

data class ExerciseResponse(
    val results: List<Exercise>
)

data class Exercise(
    val id: Int,
    val translations: List<Translation>
)

data class Translation(
    val name: String?,
    val description: String?
)
