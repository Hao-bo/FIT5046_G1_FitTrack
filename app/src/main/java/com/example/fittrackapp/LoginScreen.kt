package com.example.fittrackapp

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WelcomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
) {

    var emailValue by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Form Validation
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val isSignInSuccessful by authViewModel.isSignInSuccessful.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var icon = if (showPassword) {
        Icons.Filled.Visibility
    } else {
        Icons.Filled.VisibilityOff
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun validateForm(): Boolean {
        var isValid = true

        // validate email
        if (emailValue.trim().isEmpty()) {
            emailError = "Email address cannot be null"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue.trim()).matches()) {
            emailError = "Please enter a valid email address"
            isValid = false
        } else {
            emailError = null
        }

        // validate password
        if (password.trim().isEmpty()) {
            passwordError = "password can not be null"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "password digits at least 6"
            isValid = false
        } else {
            passwordError = null
        }

        // validate fail
        if (!isValid) {
            scope.launch {
                snackbarHostState.showSnackbar("Please fill in all required fields correctly")
            }
        }

        return isValid
    }

    // Go to home page
    LaunchedEffect(isSignInSuccessful) {
        if (isSignInSuccessful) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
            authViewModel.resetSignInSuccessFlag()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.clearErrorMessage()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(300.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Welcome to Fit Track", fontSize = 30.sp,
                        color = androidx.compose.ui.graphics.Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(64.dp))
                    TextField(
                        label = { Text(text = "Email: example@gmail.com") },
                        value = emailValue,
                        onValueChange = {
                            emailValue = it
                            emailError = null
                        },
                        modifier = Modifier.width(280.dp),
                        shape = RoundedCornerShape(16.dp),
                        isError = emailError != null,
                        supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }

                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        label = { Text("Password:") },
                        placeholder = { Text("Password") },
                        modifier = Modifier.width(280.dp),
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    icon,
                                    contentDescription = "Visibility icon"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        isError = passwordError != null,
                        supportingText = passwordError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row {
                        Button(
                            onClick = {
                                if (validateForm()) {
                                    authViewModel.signInWithEmailAndPassword(emailValue.trim(), password.trim())
                                }
                            },
                            modifier = Modifier.width(130.dp),
                            enabled = !isLoading
                        ) {
                            Text("Sign in")
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Button(
                            onClick = {
                                if (validateForm()){
                                    authViewModel.signUpWithEmailAndPassword(emailValue.trim(), password.trim())
                                }
                            },
                            modifier = Modifier.width(130.dp),
                            enabled = !isLoading
                        ) {
                            Text("Sign up")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (isLoading) return@Button
                            authViewModel.clearErrorMessage() // clean previous error message
                            scope.launch {
                                try {
                                    val credentialManager = CredentialManager.create(context)
                                    val googleSignInRequest = authViewModel.createGoogleSignInRequest()
                                    val result = credentialManager.getCredential(
                                        request = googleSignInRequest,
                                        context = context as Activity
                                    )
                                    authViewModel.processGoogleSignIn(result)
                                } catch (e: GetCredentialException) {
                                    authViewModel.handleGoogleSignInError(e)
                                }
                            }
                        },
                        modifier = Modifier.width(280.dp),
                        enabled = !isLoading
                    ) {
                        Text("Sign In / Sign Up with Google")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier.width(280.dp),
                    ) {
                        Text("Forget password")
                    }

                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Logging in...", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
