package com.example.fittrackapp.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fittrackapp.di.Graph
import com.example.fittrackapp.data.repository.ExerciseRepository
import com.example.fittrackapp.data.model.ExerciseInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the ExerciseDetailScreen.
 * It is responsible for fetching exercise information from the repository
 * and providing it to the UI through a StateFlow.
 *
 * @param repository The repository to fetch exercise data from.
 */
class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {

    // Predefined Exercise IDs for each muscle group
    // Companion object to hold predefined constants, such as exercise IDs for quick access.
    companion object {
        const val CHEST_EXERCISE_ID = 538
        const val ABS_EXERCISE_ID = 1091
        const val ARMS_EXERCISE_ID = 197
        const val LEGS_EXERCISE_ID = 203  
    }

    // MutableStateFlow to hold the current UI state of the exercise detail screen.
    // It starts in a Loading state.
    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Loading)
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    /**
     * Fetches exercise information for the given exercise ID.
     * Updates the uiState to Loading, then Success with the data, or Error if fetching fails.
     *
     * @param id The unique identifier of the exercise to fetch.
     */
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

/**
 * Represents the different states of the Exercise Detail UI.
 */
sealed class ExerciseUiState {
    /**
     * Indicates that exercise data is currently being loaded.
     */
    object Loading : ExerciseUiState()
    /**
     * Indicates that exercise data has been successfully loaded.
     * @param exerciseInfo The detailed information about the exercise.
     */
    data class Success(val exerciseInfo: ExerciseInfo) : ExerciseUiState()
    /**
     * Indicates that an error occurred while trying to load exercise data.
     * @param message A descriptive message about the error.
     */
    data class Error(val message: String) : ExerciseUiState()
}

/**
 * Factory for creating instances of [ExerciseViewModel].
 * This is necessary if the ViewModel has constructor dependencies (like the repository).
 */
class ExerciseViewModelFactory : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `Class`.
     *
     * @param modelClass a `Class` whose instance is requested
     * @return a newly created ViewModel
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class is assignable from ExerciseViewModel.
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            // If so, create and return an instance of ExerciseViewModel,
            // injecting the ExerciseRepository from the Graph (dependency container).
            return ExerciseViewModel(Graph.exerciseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}