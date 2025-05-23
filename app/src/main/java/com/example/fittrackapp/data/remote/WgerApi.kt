package com.example.fittrackapp.data.remote

import com.example.fittrackapp.data.model.ExerciseInfo
import retrofit2.http.GET
import retrofit2.http.Path

interface WgerApi {
    @GET("exerciseinfo/{id}")
    suspend fun getExerciseInfo(@Path("id") id: Int): ExerciseInfo
}