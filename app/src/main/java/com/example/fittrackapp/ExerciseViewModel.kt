package com.example.fittrackapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fittrackapp.data.ExerciseRepository
import com.example.fittrackapp.data.ExerciseInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {

    // Predefined Exercise IDs for each muscle group
    companion object {
        const val CHEST_EXERCISE_ID = 538
        const val ABS_EXERCISE_ID = 1091
        const val ARMS_EXERCISE_ID = 197
        const val LEGS_EXERCISE_ID = 203  
    }

    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Loading)
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    fun getExerciseInfo(id: Int) {
        _uiState.value = ExerciseUiState.Loading
        viewModelScope.launch {
            repository.getExerciseInfo(id).fold(
                onSuccess = { exercise ->
                    _uiState.value = ExerciseUiState.Success(exercise)
                },
                onFailure = { error ->
                    _uiState.value = ExerciseUiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}

sealed class ExerciseUiState {
    object Loading : ExerciseUiState()
    data class Success(val exerciseInfo: ExerciseInfo) : ExerciseUiState()
    data class Error(val message: String) : ExerciseUiState()
}

class ExerciseViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            return ExerciseViewModel(Graph.exerciseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}