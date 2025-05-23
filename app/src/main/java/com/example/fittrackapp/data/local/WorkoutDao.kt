package com.example.fittrackapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fittrackapp.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun addWorkout(workoutEntity: WorkoutSession)

    @Query("Select * from WorkoutSession Where `user-id` = :userId Order by timestamp DESC")
    abstract fun getAllWorkouts(userId: String): Flow<List<WorkoutSession>>

    @Update
    abstract suspend fun updateWorkout(workoutEntity: WorkoutSession)

    @Delete
    abstract suspend fun deleteWorkout(workoutEntity: WorkoutSession)

    @Query("Select * from WorkoutSession where id = :id AND `user-id` = :userId")
    abstract fun getWorkoutById(id: Int, userId: String): Flow<WorkoutSession>

    @Query("Select * from WorkoutSession where synced = 0")
    abstract fun getUnsyncedWorkouts(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM WorkoutSession WHERE `user-id` = :userId AND  timestamp BETWEEN :monthStart AND :monthEnd ORDER BY timestamp ASC")
    abstract fun getWorkoutsByMonth(userId: String, monthStart: Long, monthEnd: Long): Flow<List<WorkoutSession>>

    @Query("Select * from WorkoutSession where `user-id` = :userId AND timestamp BETWEEN :dayStart AND :dayEnd ORDER BY timestamp ASC")
    abstract fun getWorkoutByDate(userId: String, dayStart: Long, dayEnd: Long): Flow<List<WorkoutSession>>

}