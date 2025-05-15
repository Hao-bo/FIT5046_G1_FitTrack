package com.example.fittrackapp

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
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // This will be true upon successful sign-in.
    private val _isSignInSuccessful = MutableStateFlow(false)
    val isSignInSuccessful: StateFlow<Boolean> = _isSignInSuccessful.asStateFlow()

    internal val auth: FirebaseAuth = Firebase.auth
    private val credentialManager: CredentialManager = CredentialManager.create(getApplication())


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
        }
    }

    fun createGoogleSignInRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true) // Only show accounts previously used to sign in.
            .setServerClientId(getApplication<Application>().getString(R.string.default_web_client_id))
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    fun processGoogleSignIn(result: GetCredentialResponse) {
        // Update the UI state
        _isLoading.value = true
        _errorMessage.value = null

        // Get credential from Response
        val credential = result.credential
        // Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                // Create Google ID Token
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
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

    private fun firebaseAuthWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    _username.value = auth.currentUser?.displayName
                    _isSignInSuccessful.value = true
                } else {
                    // If sign in fails, display a message to the user
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    _errorMessage.value = "Firebase auth fail: ${task.exception?.message}"
                }
                _isLoading.value = false
            }
    }

    fun handleGoogleSignInError(e: GetCredentialException) {
        Log.e(TAG, "GetCredentialException", e)
        _errorMessage.value = "Google login fail: ${e.message}"
        _isLoading.value = false
    }

    fun resetSignInSuccessFlag() {
        _isSignInSuccessful.value = false
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun signOut() {
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
            _isSignInSuccessful.value = false // reset state or navigate
            _isLoading.value = false
            _errorMessage.value = null
        }
    }

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
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    _errorMessage.value = "Authentication failed: ${task.exception?.message}"
                }
                _isLoading.value = false
            }
    }

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
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    _errorMessage.value = "Registration failed: ${task.exception?.message}"
                }
                _isLoading.value = false
            }
    }
}
