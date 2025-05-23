package com.example.fittrackapp.ui.auth

import android.app.Application
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrackapp.di.Graph
import com.example.fittrackapp.R
import com.example.fittrackapp.data.model.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for authentication-related logic, including sign-in, sign-up,
 * sign-out, and managing user profile data with Firebase and Firestore.
 *
 * @param application The application context.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    internal val auth: FirebaseAuth = Firebase.auth
    private val credentialManager: CredentialManager = CredentialManager.Companion.create(getApplication())

    private var _currentUserId = MutableStateFlow<String?>(auth.currentUser?.uid)
    var currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // This will be true upon successful sign-in.
    private val _isSignInSuccessful = MutableStateFlow(false)
    val isSignInSuccessful: StateFlow<Boolean> = _isSignInSuccessful.asStateFlow()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()



    // Define a tag to find and filter log output in Logcat
    companion object {
        private const val TAG = "AuthViewModel"
    }

    init {
        // check current user and set username
        auth.currentUser?.let { user ->
            if (user.email != null) {
                _username.value = user.email?.substringBefore("@")?.take(5)
            } else {
                _username.value = user.displayName
            }
            loadUserProfile(user.uid)
        }
        // Monitor changes in user data in the user repository
        viewModelScope.launch {
            Graph.userRepository.currentUser.collectLatest { user ->
                _userProfile.value = user
            }
        }
    }

    /**
     * Updates the [_currentUserId] StateFlow with the current Firebase user's UID.
     * This function is called after successful sign-in or sign-up.
     */
    private fun updateCurrentUserId() {
        Log.d(TAG,"update function UserID: ${auth.currentUser?.uid}")
        _currentUserId.value = auth.currentUser?.uid
        Log.d(TAG,"update function UserID: ${_currentUserId.value}")
        currentUserId = _currentUserId.asStateFlow()
        Log.d(TAG,"update function UserID: ${currentUserId.value}")
    }

    /**
     * Loads the user profile from Firestore based on the provided userId.
     * If the user profile doesn't exist, it creates a new one.
     * Updates the last login time for existing users.
     *
     * @param userId The unique ID of the user.
     */
    private fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            val result = Graph.userRepository.getUserById(userId)
            result.fold(
                onSuccess = { user ->
                    if (user == null) {
                        // The user does not exist, create a new user
                        createUserInFirestore(auth.currentUser)
                    } else {
                        _userProfile.value = user
                        // Update last login time
                        Graph.userRepository.updateUserFields(userId, mapOf("lastLoginAt" to System.currentTimeMillis()))
                    }
                },
                onFailure = { e ->
                    Log.e(TAG, "Error loading user profile", e)
                    _errorMessage.value = "Failed to load user profile: ${e.message}"
                }
            )
        }
    }

    /**
     * Creates a new user document in Firestore using the details from the FirebaseUser object.
     *
     * @param firebaseUser The FirebaseUser object containing user details.
     */
    private fun createUserInFirestore(firebaseUser: FirebaseUser?) {
        firebaseUser?.let { user ->
            val newUser = User(
                userId = user.uid,
                email = user.email,
                displayName = user.displayName ?: user.email?.substringBefore("@")?.take(5),
                photoUrl = user.photoUrl?.toString(),
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )

            viewModelScope.launch {
                // Attempt to create or update the user in the repository.
                val result = Graph.userRepository.createOrUpdateUser(newUser)
                result.fold(
                    onSuccess = { createdUser ->
                        Log.d(TAG, "User created in Firestore: ${createdUser.userId}")
                    },
                    onFailure = { e ->
                        Log.e(TAG, "Error creating user in Firestore", e)
                        _errorMessage.value = "Failed to create user profile: ${e.message}"
                    }
                )
            }
        }
    }

    /**
     * Updates specified fields of the current user's profile in Firestore.
     *
     * @param fields A map where keys are field names and values are the new field values.
     */
    fun updateUserProfile(fields: Map<String, Any>) {
        // Get the current user's ID; return if no user is signed in.
        val userId = _currentUserId.value ?: return

        viewModelScope.launch {
            // Attempt to update user fields in the repository.
            val result = Graph.userRepository.updateUserFields(userId, fields)
            result.fold(
                onSuccess = { success ->
                    if (success) {
                        Log.d(TAG, "User profile updated successfully")
                    }
                },
                onFailure = { e ->
                    Log.e(TAG, "Error updating user profile", e)
                    _errorMessage.value = "Failed to update profile: ${e.message}"
                }
            )
        }
    }

    /**
     * Creates a [androidx.credentials.GetCredentialRequest] for Google Sign-In.
     * Configures the request to filter by authorized accounts and use the web client ID.
     *
     * @return A [androidx.credentials.GetCredentialRequest] object.
     */
    fun createGoogleSignInRequest(): GetCredentialRequest {
        // Configure Google ID options for the sign-in request.
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true) // Only show accounts previously used to sign in.
            .setServerClientId(getApplication<Application>().getString(R.string.default_web_client_id))
            .build()

        // Build the GetCredentialRequest with the Google ID option.
        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    /**
     * Processes the [androidx.credentials.GetCredentialResponse] received from a successful Google Sign-In attempt.
     * Extracts the Google ID token and uses it to sign in with Firebase.
     *
     * @param result The [androidx.credentials.GetCredentialResponse] from the Google Sign-In flow.
     */
    fun processGoogleSignIn(result: GetCredentialResponse) {
        // Update the UI state
        _isLoading.value = true
        _errorMessage.value = null

        // Get credential from Response
        val credential = result.credential
        // Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                // Create Google ID Token
                val googleIdTokenCredential = GoogleIdTokenCredential.Companion.createFrom(credential.data)
                // Sign in to Firebase with using the token
                firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
            } catch (e: Exception) {
                Log.e(TAG, "Google ID Token create fail", e)
                _errorMessage.value = "process Google credential fail: ${e.message}"
                _isLoading.value = false
            }
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
            _errorMessage.value = "Unexpected credential type: ${credential.type}"
            _isLoading.value = false
        }
    }

    /**
     * Authenticates with Firebase using a Google ID token.
     * On success, updates user information and sign-in status.
     * On failure, updates the error message.
     *
     * @param idToken The Google ID token string.
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    _username.value = auth.currentUser?.displayName
                    _isSignInSuccessful.value = true
                    updateCurrentUserId()
                } else {
                    // If sign in fails, display a message to the user
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    _errorMessage.value = "Firebase auth fail: ${task.exception?.message}"
                }
                _isLoading.value = false
            }
    }

    /**
     * Handles errors that occur during the Google Sign-In [GetCredentialRequest].
     *
     * @param e The [androidx.credentials.exceptions.GetCredentialException] that occurred.
     */
    fun handleGoogleSignInError(e: GetCredentialException) {
        Log.e(TAG, "GetCredentialException", e)
        _errorMessage.value = "Google login fail: ${e.message}"
        _isLoading.value = false
    }

    /**
     * Resets the [_isSignInSuccessful] flag to false.
     * Typically called by the UI after navigating upon successful sign-in.
     */
    fun resetSignInSuccessFlag() {
        _isSignInSuccessful.value = false
    }

    /**
     * Clears any existing error message by setting [_errorMessage] to null.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }


    /**
     * Signs out the current user from Firebase and clears credential state.
     * Resets relevant ViewModel states.
     */
    fun signOut() {
        Log.d(TAG, "UserID: ${auth.currentUser?.uid}")
        _userProfile.value = null
        auth.signOut()
        // When a user signs out, clear the current user credential state from all credential providers.
        viewModelScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
            } catch (e: ClearCredentialException) {
                Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
            }
            Log.d(TAG, "user has been signed out in Firebase")
            _currentUserId.value = null
            _username.value = null
            _isSignInSuccessful.value = false // reset state or navigate
            _isLoading.value = false
            _errorMessage.value = null

            Graph.formViewModel.clearSelectedDate()
            Log.d(TAG, "UserID: ${auth.currentUser?.uid}")
        }
    }

    /**
     * Signs in a user with their email and password using Firebase Authentication.
     *
     * @param email The user's email address.
     * @param password The user's password.
     */
    // Regular sign in and sign up
    fun signInWithEmailAndPassword(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    _username.value = email.substringBefore("@").take(5)
                    _isSignInSuccessful.value = true
                    updateCurrentUserId()

                    // load User profile
                    auth.currentUser?.let { user ->
                        loadUserProfile(user.uid)
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    _errorMessage.value = "Authentication failed: ${task.exception?.message}"
                }
                _isLoading.value = false
            }
    }

    /**
     * Registers a new user with their email and password using Firebase Authentication.
     *
     * @param email The new user's email address.
     * @param password The new user's password.
     */
    fun signUpWithEmailAndPassword(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    _username.value = email.substringBefore("@").take(5)
                    _isSignInSuccessful.value = true
                    updateCurrentUserId()
                    // load User profile
                    auth.currentUser?.let { user ->
                        loadUserProfile(user.uid)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    _errorMessage.value = "Registration failed: ${task.exception?.message}"
                }
                _isLoading.value = false
            }
    }
}