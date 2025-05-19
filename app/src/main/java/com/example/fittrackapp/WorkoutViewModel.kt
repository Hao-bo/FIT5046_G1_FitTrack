package com.example.fittrackapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrackapp.Graph.authViewModel
import com.example.fittrackapp.data.WorkoutRepository
import com.example.fittrackapp.data.WorkoutSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository = Graph.workoutRepository,
) : ViewModel() {


    fun saveWorkout(timestamp: Long, durationMinutes: Int, activityType: String, notes: String?) {

        viewModelScope.launch(Dispatchers.IO) {

            authViewModel.currentUserId.value?.let { userId ->
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