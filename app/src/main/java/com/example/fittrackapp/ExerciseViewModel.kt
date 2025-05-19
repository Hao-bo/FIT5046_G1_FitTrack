package com.example.fittrackapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    init {
        fetchExercises()
    }

    private fun fetchExercises() {
        viewModelScope.launch {
            println("Start calling Retrofit")
            try {
                val response = RetrofitClient.api.getExercises()
                println("Get the number of courses: ${response.results.size}")
                _exercises.value = response.results
            } catch (e: Exception) {
                println("Retrofit request failed: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
