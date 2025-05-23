package com.example.fittrackapp.data.model

data class User(
    val userId: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val height: Double? = null,
    val weight: Double? = null,
    val fitnessGoal: String? = null,
    val weeklyWorkoutTarget: Int? = null
) {
    constructor() : this("", null, null)
}