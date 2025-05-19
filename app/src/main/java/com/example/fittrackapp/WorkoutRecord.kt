package com.example.fittrackapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_records")
data class WorkoutRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val exerciseName: String,
    val durationMinutes: Int
)
