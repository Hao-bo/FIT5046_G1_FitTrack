package com.example.fittrackapp.data

import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    suspend fun addWorkout(workout: WorkoutSession) {
        workoutDao.addWorkout(workout)
    }

    fun getAllWorkouts(): Flow<List<WorkoutSession>> {
        return workoutDao.getAllWorkouts()
    }

    fun getWorkoutById(id: Int): Flow<WorkoutSession> {
        return workoutDao.getWorkoutById(id)
    }

    fun getWorkoutsByMonth(monthStart: Long, monthEnd: Long): Flow<List<WorkoutSession>> {
        return workoutDao.getWorkoutsByMonth(monthStart, monthEnd)
    }

    fun getWorkoutsByDate(dayStart: Long, dayEnd: Long): Flow<List<WorkoutSession>> {
        return workoutDao.getWorkoutByDate(dayStart, dayEnd)
    }

    suspend fun updateWorkout(workout: WorkoutSession) {
        workoutDao.updateWorkout(workout)

    }

    suspend fun deleteWorkout(workout: WorkoutSession) {
        workoutDao.deleteWorkout(workout)
    }
}