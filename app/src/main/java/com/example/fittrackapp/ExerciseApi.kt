package com.example.fittrackapp.network

import com.example.fittrackapp.ExerciseResponse
import retrofit2.http.GET

interface ExerciseApi {
    @GET("exercise/?language=2&limit=20")
    suspend fun getExercises(): ExerciseResponse
}
