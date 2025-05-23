package com.example.fittrackapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = Graph.authViewModel
) {
    val scope = rememberCoroutineScope()
    val userName by authViewModel.username.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    // Edit mode state
    var isEditMode by remember { mutableStateOf(false) }
    var showSavedMessage by remember { mutableStateOf(false) }

    // User Profile Fields
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var fitnessGoal by remember { mutableStateOf("") }
    var weeklyWorkoutTarget by remember { mutableStateOf("") }

    // Initialize form data
    LaunchedEffect(userProfile) {
        userProfile?.let { user ->
            height = user.height?.toString() ?: ""
            weight = user.weight?.toString() ?: ""
            fitnessGoal = user.fitnessGoal ?: ""
            weeklyWorkoutTarget = user.weeklyWorkoutTarget?.toString() ?: ""
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "return",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )

                    Text(
                        text = if (isEditMode) "edit your profile" else "profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (!isEditMode) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "edit",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    isEditMode = true
                                }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.size(24.dp))

                // Avatar and username section
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = userName ?: "User",
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )

                userProfile?.email?.let { email ->
                    Text(
                        text = email,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.size(32.dp))

                // Edit Mode Messages
                AnimatedVisibility(
                    visible = showSavedMessage,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = "Data has been saved！",
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    LaunchedEffect(showSavedMessage) {
                        delay(3000)
                        showSavedMessage = false
                    }
                }

                // Profile Information
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Fitness Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (isEditMode) {
                            // Edit Mode
                            OutlinedTextField(
                                value = height,
                                onValueChange = { height = it },
                                label = { Text("height (cm)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = weight,
                                onValueChange = { weight = it },
                                label = { Text("weight (kg)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = fitnessGoal,
                                onValueChange = { fitnessGoal = it },
                                label = { Text("Fitness goals") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = weeklyWorkoutTarget,
                                onValueChange = { weeklyWorkoutTarget = it },
                                label = { Text("Weekly training goals") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                singleLine = true
                            )
                        } else {
                            // Display Mode
                            ProfileInfoItem(
                                label = "height",
                                value = if (userProfile?.height != null) "${userProfile?.height} cm" else "Not set"
                            )

                            ProfileInfoItem(
                                label = "weight",
                                value = if (userProfile?.weight != null) "${userProfile?.weight} kg" else "Not set"
                            )

                            ProfileInfoItem(
                                label = "Fitness goals",
                                value = userProfile?.fitnessGoal ?: "Not set"
                            )

                            ProfileInfoItem(
                                label = "Weekly training goals",
                                value = if (userProfile?.weeklyWorkoutTarget != null) "${userProfile?.weeklyWorkoutTarget} times" else "Not set"
                            )
                        }
                    }
                }

                // Button area
                if (isEditMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = {
                                isEditMode = false
                                // Reset to original value
                                userProfile?.let { user ->
                                    height = user.height?.toString() ?: ""
                                    weight = user.weight?.toString() ?: ""
                                    fitnessGoal = user.fitnessGoal ?: ""
                                    weeklyWorkoutTarget = user.weeklyWorkoutTarget?.toString() ?: ""
                                }
                            },
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                // Save changes
                                val updatedFields = mutableMapOf<String, Any>()

                                height.toDoubleOrNull()?.let { updatedFields["height"] = it }
                                weight.toDoubleOrNull()?.let { updatedFields["weight"] = it }
                                if (fitnessGoal.isNotBlank()) updatedFields["fitnessGoal"] = fitnessGoal
                                weeklyWorkoutTarget.toIntOrNull()?.let { updatedFields["weeklyWorkoutTarget"] = it }

                                scope.launch {
                                    authViewModel.updateUserProfile(updatedFields)
                                    isEditMode = false
                                    showSavedMessage = true
                                }
                            },
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Save")
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            scope.launch {
                                authViewModel.signOut()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            "Log out",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Loading Indicator
            if (isLoading && !isEditMode) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
//    ProfileScreen(navController = rememberNavController())
}