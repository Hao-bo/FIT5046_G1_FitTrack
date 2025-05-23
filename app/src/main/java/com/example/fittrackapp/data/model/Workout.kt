package com.example.fittrackapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "user-id")
    val userId: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long, // workout time
    @ColumnInfo(name = "duration-minutes")
    val durationMinutes: Int, // duration time
    @ColumnInfo(name = "activity-type")
    val activityType: String, // eg. “Yoga”, “HIIT”
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "synced")
    val synced: Boolean = false // synchronized to Firebase state?
)
