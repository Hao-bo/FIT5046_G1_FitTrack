package com.example.fittrackapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrackapp.data.WorkoutRepository
import com.example.fittrackapp.data.WorkoutSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WorkoutViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {



    fun saveWorkout(timestamp: Long, durationMinutes: Int, activityType: String, notes: String?) {
        // create WorkoutSession object
        val newWorkout = WorkoutSession(
            timestamp = timestamp,
            durationMinutes = durationMinutes,
            activityType = activityType,
            notes = notes,
            synced = false
        )

        viewModelScope.launch(Dispatchers.IO) {
            workoutRepository.addWorkout(newWorkout)
        }
    }
}