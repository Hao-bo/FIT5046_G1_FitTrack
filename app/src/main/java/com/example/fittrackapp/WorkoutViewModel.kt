package com.example.fittrackapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrackapp.data.WorkoutDao
import com.example.fittrackapp.model.WorkoutRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(private val dao: WorkoutDao) : ViewModel() {

    private val _records = MutableStateFlow<List<WorkoutRecord>>(emptyList())
    val records: StateFlow<List<WorkoutRecord>> = _records

    init {
        loadRecords()
    }

    fun insertRecord(record: WorkoutRecord) {
        viewModelScope.launch {
            dao.insert(record)
            loadRecords()
        }
    }

    private fun loadRecords() {
        viewModelScope.launch {
            dao.getAllRecords().collect { list ->
                _records.value = list
            }
        }
    }

}

