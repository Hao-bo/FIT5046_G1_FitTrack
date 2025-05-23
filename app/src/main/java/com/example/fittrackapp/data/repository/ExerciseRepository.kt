package com.example.fittrackapp.data.repository

import com.example.fittrackapp.data.remote.ApiClient
import com.example.fittrackapp.data.model.ExerciseInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExerciseRepository {
    private val api = ApiClient.wgerApi

    suspend fun getExerciseInfo(id: Int): Result<ExerciseInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getExerciseInfo(id)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}