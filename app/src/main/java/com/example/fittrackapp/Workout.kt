package com.example.fittrackapp

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long, // workout time
    val durationMinutes: Int, // duration time
    val activityType: String, // eg. “Yoga”, “HIIT”
    val notes: String? = null,
    val synced: Boolean = false // synchronized to Firebase state?
)
