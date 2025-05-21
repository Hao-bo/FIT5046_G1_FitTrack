package com.example.fittrackapp.data

import com.example.fittrackapp.data.ApiClient
import com.example.fittrackapp.data.ExerciseInfo
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