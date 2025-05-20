package com.example.fittrackapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fittrackapp.WorkoutDao

class WorkoutViewModelFactory(private val dao: WorkoutDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
