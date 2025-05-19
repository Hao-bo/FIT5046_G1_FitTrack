package com.example.fittrackapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addWorkout(workoutEntity: WorkoutSession)

    @Query("Select * from WorkoutSession")
    abstract fun getAllWorkouts(): Flow<List<WorkoutSession>>

    @Update
    abstract fun updateWorkout(workoutEntity: WorkoutSession)

    @Delete
    abstract fun deleteWorkout(workoutEntity: WorkoutSession)

    @Query("Select * from WorkoutSession where id = :id")
    abstract fun getWorkoutById(id: Int): Flow<WorkoutSession>

    @Query("Select * from WorkoutSession where synced = 0")
    abstract fun getUnsyncedWorkouts(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM WorkoutSession WHERE timestamp BETWEEN :monthStart AND :monthEnd ORDER BY timestamp ASC")
    abstract fun getWorkoutsByMonth(monthStart: Long, monthEnd: Long): Flow<List<WorkoutSession>>

    @Query("Select * from WorkoutSession where timestamp BETWEEN :dayStart AND :dayEnd ORDER BY timestamp ASC")
    abstract fun getWorkoutByDate(dayStart: Long, dayEnd: Long): Flow<List<WorkoutSession>>

}