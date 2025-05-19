package com.example.fittrackapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fittrackapp.model.WorkoutRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insert(record: WorkoutRecord)

    @Query("SELECT * FROM workout_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<WorkoutRecord>>
}
