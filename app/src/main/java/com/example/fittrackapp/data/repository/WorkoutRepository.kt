package com.example.fittrackapp.data.repository

import com.example.fittrackapp.data.local.WorkoutDao
import com.example.fittrackapp.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    suspend fun addWorkout(workout: WorkoutSession) {
        workoutDao.addWorkout(workout)
    }

    fun getAllWorkouts(userId: String): Flow<List<WorkoutSession>> {
        return workoutDao.getAllWorkouts(userId)
    }

    fun getWorkoutById(id: Int, userId: String): Flow<WorkoutSession> {
        return workoutDao.getWorkoutById(id, userId)
    }

    fun getWorkoutsByMonth(userId: String,monthStart: Long, monthEnd: Long): Flow<List<WorkoutSession>> {
        return workoutDao.getWorkoutsByMonth(userId,monthStart, monthEnd)
    }

    fun getWorkoutsByDate(userId: String,dayStart: Long, dayEnd: Long): Flow<List<WorkoutSession>> {
        return workoutDao.getWorkoutByDate(userId,dayStart, dayEnd)
    }

    suspend fun updateWorkout(workout: WorkoutSession) {
        workoutDao.updateWorkout(workout)
    }

    suspend fun deleteWorkout(workout: WorkoutSession) {
        workoutDao.deleteWorkout(workout)
    }
}