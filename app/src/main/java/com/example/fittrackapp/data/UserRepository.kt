package com.example.fittrackapp.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: Flow<User?> = _currentUser.asStateFlow()

    companion object {
        private const val TAG = "UserRepository"
    }

    // Create or update a user
    suspend fun createOrUpdateUser(user: User) = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(user.userId)
                .set(user, SetOptions.merge())
                .await()
            _currentUser.value = user
            Log.d(TAG, "User document created/updated for ${user.userId}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating/updating user document", e)
            Result.failure<User>(e)
        }
    }

    // Get user information
    suspend fun getUserById(userId: String): Result<User?> = withContext(Dispatchers.IO) {
        try {
            val documentSnapshot = usersCollection.document(userId).get().await()
            val user = documentSnapshot.toObject(User::class.java)

            if (user != null) {
                _currentUser.value = user
                Log.d(TAG, "User document retrieved for $userId")
                Result.success(user)
            } else {
                Log.d(TAG, "No user document found for $userId")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user document", e)
            Result.failure<User?>(e)
        }
    }

    // Update user specific fields
    suspend fun updateUserFields(userId: String, fields: Map<String, Any>): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(userId)
                .update(fields)
                .await()

            // Update local cached user data
            val currentUserValue = _currentUser.value
            if (currentUserValue != null && currentUserValue.userId == userId) {
                // Get the latest user data
                getUserById(userId)
            }

            Log.d(TAG, "User fields updated for $userId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user fields", e)
            Result.failure<Boolean>(e)
        }
    }

    // Deleting a User
    suspend fun deleteUser(userId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(userId)
                .delete()
                .await()

            if (_currentUser.value?.userId == userId) {
                _currentUser.value = null
            }

            Log.d(TAG, "User document deleted for $userId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user document", e)
            Result.failure<Boolean>(e)
        }
    }
}