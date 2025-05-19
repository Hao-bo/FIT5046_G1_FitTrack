package com.example.fittrackapp

import com.example.fittrackapp.ExerciseResponse
import retrofit2.http.GET

interface ExerciseApi {
    @GET("exerciseinfo/?language=2&limit=20")
    suspend fun getExercises(): ExerciseResponse
}
