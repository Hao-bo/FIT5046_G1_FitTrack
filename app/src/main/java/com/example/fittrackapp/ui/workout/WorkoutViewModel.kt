package com.example.fittrackapp.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrackapp.di.Graph
import com.example.fittrackapp.data.model.WorkoutSession
import com.example.fittrackapp.data.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for screens related to creating or managing individual workout sessions.
 * It handles the logic for saving new workout sessions to the repository.
 *
 * @param workoutRepository The repository for accessing workout data.
 * Defaults to the instance provided by Graph.workoutRepository.
 */
class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository = Graph.workoutRepository,
) : ViewModel() {


    /**
     * Saves a new workout session to the repository.
     * This function is launched in a coroutine on the IO dispatcher for database operations.
     * It retrieves the current user ID from the globally accessible AuthViewModel in Graph.
     *
     * @param timestamp The timestamp (in milliseconds since epoch) when the workout occurred.
     * @param durationMinutes The duration of the workout in minutes.
     * @param activityType The type of activity performed (e.g., "Running", "Yoga").
     * @param notes Optional notes or comments about the workout.
     */
    fun saveWorkout(timestamp: Long, durationMinutes: Int, activityType: String, notes: String?) {

        // Launch a coroutine in the viewModelScope using the IO dispatcher,
        // suitable for background tasks like database operations.
        viewModelScope.launch(Dispatchers.IO) {

            // Get the current user ID from the authViewModel.
            // If the userId is null (no user logged in), the workout won't be saved.
            Graph.authViewModel.currentUserId.value?.let { userId ->
                // Create WorkoutSession object with userId
                val newWorkout = WorkoutSession(
                    userId = userId,
                    timestamp = timestamp,
                    durationMinutes = durationMinutes,
                    activityType = activityType,
                    notes = notes,
                    synced = false
                )

                workoutRepository.addWorkout(newWorkout)
            }
        }
    }
}